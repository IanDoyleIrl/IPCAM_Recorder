package org.test.cameraMonitor.streamingServer;

import net.sf.jipcam.axis.MjpegFrame;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.*;
import org.test.cameraMonitor.recordingEngine.IPCameraTest;
import org.test.cameraMonitor.util.APIUtils;
import org.test.cameraMonitor.util.ImageUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 13/01/2013
 * Time: 11:22
 * To change this template use File | Settings | File Templates.
 */
public class StreamingUtils {

    private static final String boundry = "--myboundary";
    private static final byte[] b = boundry.getBytes();

    public static void handleRecordedStreaming (HttpServletResponse response, HttpServletRequest request, RecordedStream recordedStream) throws IOException {
        //response.setContentLength((int) pdfFile.length());
        List streamData = recordedStream.getStream();
        Iterator<RecordedImage> iterator = streamData.iterator();
        String boundry = "--myboundary";
        String empty = "\r\n";
        byte[] b = boundry.getBytes();
        //FileInputStream fileInputStream = new FileInputStream(pdfFile);
        OutputStream responseOutputStream = response.getOutputStream();
        response.setContentType("multipart/x-mixed-replace; boundary=--myboundary");
        responseOutputStream.flush();
        long frameTime = 0;
        RecordedImage image = null;
        while ((iterator.hasNext())) {
            Image i = iterator.next();
            byte[] imageData = ImageUtils.putTimpStampOnImage(i, APIUtils.getTimestampFromLong(i.getDate())).getImageData();
            StreamingUtils.sendMJPEGFrame(responseOutputStream, imageData);
            try {
                Thread.sleep(1000 / Integer.parseInt(GlobalAttributes.getInstance().getConfigValue("FramesPerSecond")));
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public static void handleEventStreaming (HttpServletResponse response, HttpServletRequest request, Event event) throws IOException {
        //response.setContentLength((int) pdfFile.length());
        List streamData = event.getStream();
        Iterator<RecordedImage> iterator = streamData.iterator();
        String boundry = "--myboundary";
        String empty = "\r\n";
        byte[] b = boundry.getBytes();
        //FileInputStream fileInputStream = new FileInputStream(pdfFile);
        OutputStream responseOutputStream = response.getOutputStream();
        response.setContentType("multipart/x-mixed-replace; boundary=--myboundary");
        responseOutputStream.flush();
        long frameTime = 0;
        RecordedImage image = null;
        while ((iterator.hasNext())) {
            Image i = iterator.next();
            byte[] imageData = ImageUtils.putTimpStampOnImage(i, APIUtils.getTimestampFromLong(i.getDate())).getImageData();
            StreamingUtils.sendMJPEGFrame(responseOutputStream, imageData);
            try {
                Thread.sleep(1000 / Integer.parseInt(GlobalAttributes.getInstance().getConfigValue("FramesPerSecond")));
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public static void handleLiveStreaming(HttpServletResponse response, HttpServletRequest request, Camera camera) throws IOException, InterruptedException {
        boolean firstTime = true;
        HttpURLConnection connection;
        URL cam = new URL(camera.getUrl());
        connection = (HttpURLConnection)cam.openConnection();
        IPCameraTest in = new IPCameraTest(connection.getInputStream());
        OutputStream responseOutputStream = response.getOutputStream();
        response.setContentType("multipart/x-mixed-replace; boundary=--myboundary");
        responseOutputStream.flush();
        MjpegFrame frame = null;
        while ((frame = in.readMjpegFrame()) != null) {
            //System.out.println("FRM: " + frame.getJpegBytes().length);
            //System.out.println("OUT: " + frame.getBytes().length);
            sendMJPEGFrame(responseOutputStream, frame.getJpegBytes());
            Thread.sleep(1000 / Integer.parseInt(GlobalAttributes.getInstance().getConfigValue("FramesPerSecond")));

        }
    }


    private static void sendMJPEGFrame(OutputStream responseOutputStream, byte[] imageData) throws IOException{
        responseOutputStream.write(("--myboundary").getBytes());
        responseOutputStream.write(("\r\n").getBytes());
        responseOutputStream.write(("Content-Type:image/jpeg").getBytes());
        responseOutputStream.write(("\r\n").getBytes());
        responseOutputStream.write(("Content-Length:" + imageData.length).getBytes());
        responseOutputStream.write(("\r\n").getBytes());
        responseOutputStream.write(("\r\n").getBytes());
        responseOutputStream.write(imageData);
        responseOutputStream.write(b);
        responseOutputStream.flush();
    }
}
