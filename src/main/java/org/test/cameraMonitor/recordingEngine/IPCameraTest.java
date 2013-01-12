package org.test.cameraMonitor.recordingEngine; /**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 06/01/2013
 * Time: 23:00
 * To change this template use File | Settings | File Templates.
 */

import net.sf.jipcam.axis.JpegFormat;
import net.sf.jipcam.axis.MjpegFormat;
import net.sf.jipcam.axis.MjpegFrame;
import org.apache.log4j.Logger;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class IPCameraTest extends DataInputStream{


    public IPCameraTest(InputStream inputStream) {
        super(inputStream);
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
