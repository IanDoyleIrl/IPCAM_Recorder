/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 06/01/2013
 * Time: 23:00
 * To change this template use File | Settings | File Templates.
 */

import net.sf.jipcam.axis.JpegFormat;
import net.sf.jipcam.axis.MjpegFormat;
import net.sf.jipcam.axis.MjpegFrame;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

public class IPCameraTest extends DataInputStream {

    public static void main(String args[]) throws MalformedURLException {

        long elapsed;
        long start = System.currentTimeMillis();
        int count = 0;
        int compareCount = 0;
        while (count < 1000){
        try {
            HttpURLConnection connection;
            URL cam = new URL("http://172.16.1.240:8081/videostream.cgi?user=userName&pwd=Password");
            connection = (HttpURLConnection)cam.openConnection();
            connection.connect();
            connection.getInputStream();
            IPCameraTest in = new IPCameraTest(connection.getInputStream());
            MjpegFrame frame = null;
            BufferedImage originalCompareImage = null;
            //String dateTime = DateFormatUtils.format(new Date().getTime(), "DD\\MM\\yyyy - HH:MM:ss:SSSS");
            while ((frame = in.readMjpegFrame()) != null && count < 1000) {
                String dateTime = DateFormatUtils.format(new Date().getTime(), "HH:MM:ss:SSSS");
                FileOutputStream out = new FileOutputStream(new File("./CamTest/Recording/" + dateTime + ".jpg"));
                byte[] tempImage = (frame.getJpegBytes());
                ConnectionUtil.insertRecordingIntoDB(System.currentTimeMillis(), tempImage, null);
                compareCount ++;
                //System.out.println(count++ + "Name: " + dateTime + ".jpg");
                if (compareCount > 25){
                    if (originalCompareImage != null){
                       //System.out.println("Test");
                       //System.out.println(originalCompareImage + "==" + dateTime);
                       InputStream inputStream = new ByteArrayInputStream(tempImage);
                       BufferedImage bImageFromConvert = ImageIO.read(inputStream);
                       ImageCompare ic = new ImageCompare(originalCompareImage, ImageIO.read(new ByteArrayInputStream(tempImage)));
                       ic.setParameters(12, 8, 5, 10);
                       // Display some indication of the differences in the image.
                       ic.setDebugMode(0);
                       // Compare.
                       ic.compare();
                       System.out.println(ic.match());
                       if (!ic.match()){
                           System.out.println("Event Triggered");
                           ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                           //ImageIO.setUseCache(false);
                           ImageIO.write(ic.getChangeIndicator(), "jpg", outputStream);
                           byte[] imageBytes = outputStream.toByteArray();
                           ConnectionUtil.insertEventIntoDB(System.currentTimeMillis(), imageBytes, null);
                       }
                       originalCompareImage = ImageIO.read(new ByteArrayInputStream(tempImage));
                       compareCount = 0;
                       ic = null;
                    }
                    else{
                        originalCompareImage = ImageIO.read(new ByteArrayInputStream(tempImage));
                    }
                }
            }
        } catch (EOFException eof) {
            eof.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }

        elapsed = System.currentTimeMillis() - start;
        System.out.println("elapsed = " + elapsed);
        System.out.println("frame count = " + count);
        System.out.println("fps = " + ((count * 1000) / elapsed));

    }

    private static Logger mLog = Logger.getLogger(IPCameraTest.class); //logging mechanism
    protected int mSequence = 0;
    protected int mContentLength = -1;
    protected boolean isContentLengthAvailable = false;
    protected boolean isFirstPass = true;

    /**
     * Wrap the given input stream with the MjpegInputStream.  Internal buffers
     * are created automatially.
     *
     * @param in stream to read and parse MJPEG frames from
     */
    public IPCameraTest(InputStream in) {
        super(new BufferedInputStream(in, MjpegFormat.FRAME_MAX_LENGTH));
    }

    /**
     * Read the next MjpegFrame from the stream.
     *
     * @return the next MJPEG frame.
     * @throws IOException if there is an error while reading data
     */
    public MjpegFrame readMjpegFrame() throws IOException {
        //mark the start of the frame
        mark(MjpegFormat.FRAME_MAX_LENGTH);

        //get length of header
        int headerLen = MjpegFormat.getStartOfSequence(this,
                JpegFormat.SOI_MARKER); //position of first byte of the jpeg

        if (isFirstPass) {
            //attempt to parse content length
            isFirstPass = false; //do this once
            reset();

            byte[] header = new byte[headerLen];
            readFully(header);

            try {
                mContentLength = MjpegFormat.parseContentLength(header);
                isContentLengthAvailable = true; //flag for more efficientcy
            } catch (NumberFormatException nfe) {
                mLog.warn(
                        "couldn't parse content length from header on first pass");
                isContentLengthAvailable = false;
            }
        }

        reset();

        if (isContentLengthAvailable) {
            //the fast way
            byte[] header = new byte[headerLen];
            readFully(header);

            try {
                mContentLength = MjpegFormat.parseContentLength(header);
            } catch (NullPointerException npe) {
                mLog.warn(
                        "couldn't parse content length, failover to jpeg EOF search");
                mContentLength = MjpegFormat.getEndOfSeqeunce(this,
                        JpegFormat.EOF_MARKER); //position of first byte after the jpeg
            }

            //(JpegFormat.EOF_MARKER); //position of first byte after the jpeg
        } else {
            //the slow way, because we have to test (if/then) every byte!
            mContentLength = MjpegFormat.getEndOfSeqeunce(this,
                    JpegFormat.EOF_MARKER); //position of first byte AFTER the jpeg
        }

        //create frame array
        byte[] frameData = new byte[headerLen + mContentLength];
        reset();
        readFully(frameData);

        return new MjpegFrame(frameData, mContentLength, mSequence++);
    }

}
