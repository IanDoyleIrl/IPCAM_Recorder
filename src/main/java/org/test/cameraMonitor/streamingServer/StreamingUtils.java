package org.test.cameraMonitor.streamingServer;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import net.sf.jipcam.axis.MjpegFrame;
import org.apache.commons.io.IOUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.json.simple.JSONObject;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.*;
import org.test.cameraMonitor.recordingEngine.IPCameraTest;
import org.test.cameraMonitor.util.APIUtils;
import org.test.cameraMonitor.util.EventStreamingData;
import org.test.cameraMonitor.util.HibernateUtil;
import org.test.cameraMonitor.util.ImageUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

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


    public static JSONObject getJSONFromEventStreamingData(EventStream stream){
        JSONObject response = new JSONObject();
        response.put("totalFrames", stream.getTotalFrames());
        response.put("currentFrame", stream.getCurrentFrame());
        response.put("status", stream.getStatus().toString());
        double current = (double)stream.getCurrentFrame();
        double total = (double)stream.getTotalFrames();
        double divValue = current / total;
        double percentage = divValue * 100;
        response.put("percentComplete", Math.round(percentage));
        return response;
    }

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
        EventStreamingData handler = new EventStreamingData(event);
        GlobalAttributes.getInstance().getEventStreamingDataHashMap().put(event, handler);
        String boundry = "--myboundary";
        String empty = "\r\n";
        byte[] b = boundry.getBytes();
        //FileInputStream fileInputStream = new FileInputStream(pdfFile);
        OutputStream responseOutputStream = response.getOutputStream();
        response.setContentType("multipart/x-mixed-replace; boundary=--myboundary");
        responseOutputStream.flush();
        long frameTime = 0;
        RecordedImage image = null;
        List streamData = event.getStream();
        Iterator<RecordedImage> iterator = streamData.iterator();
        handler.setCurrentFrameCount(0);
        handler.setTotalFrameCount(streamData.size());
        handler.setReadyToStream(true);
        handler.update();
        while ((iterator.hasNext())) {
            Image i = iterator.next();
            byte[] imageData = ImageUtils.putTimpStampOnImage(i, APIUtils.getTimestampFromLong(i.getDate())).getImageData();
            StreamingUtils.sendMJPEGFrame(responseOutputStream, imageData);
            try {
                Thread.sleep(1000 / Integer.parseInt(GlobalAttributes.getInstance().getConfigValue("FramesPerSecond")));
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            handler.setCurrentFrameCount(handler.getCurrentFrameCount() + 1);
            handler.update();
        }
        handler.setReadyToStream(false);
        handler.update();
        GlobalAttributes.getInstance().getEventStreamingDataHashMap().remove(event);
    }

    public static void handleLiveStreaming(HttpServletResponse response, HttpServletRequest request, Camera camera) throws IOException, InterruptedException {
        String boundry = "--myboundary";
        String empty = "\r\n";
        byte[] b = boundry.getBytes();
        //FileInputStream fileInputStream = new FileInputStream(pdfFile);
        OutputStream responseOutputStream = response.getOutputStream();
        response.setContentType("multipart/x-mixed-replace; boundary=--myboundary");
        responseOutputStream.flush();
        long frameTime = 0;
        RecordedImage image = null;
        while (true) {
            DetachedCriteria maxQuery = DetachedCriteria.forClass( RecordedImage.class );
            maxQuery.setProjection( Projections.max("Id") );
            Criteria query = HibernateUtil.getSessionFactory().openSession().createCriteria( RecordedImage.class );
            query.add( Property.forName("Id").eq(maxQuery) );
            image = (RecordedImage) query.uniqueResult();
            byte[] imageData = ImageUtils.putTimpStampOnImage(image, APIUtils.getTimestampFromLong(image.getDate())).getImageData();
            sendMJPEGFrame(response.getOutputStream(), imageData);
            Thread.sleep(1000000);
        }
    }


    public static void sendMJPEGFrame(OutputStream responseOutputStream, byte[] imageData) throws IOException{
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


    public static void sendLoadingMJPEGFrame(OutputStream responseOutputStream) throws IOException{
        File loadingImage = new File("/Users/Ian/Downloads/loading.gif");
        FileInputStream stream = new FileInputStream(loadingImage);
        boolean test = loadingImage.canRead();
        byte[] data = IOUtils.toByteArray(stream);
        responseOutputStream.write(("--myboundary").getBytes());
        responseOutputStream.write(("\r\n").getBytes());
        responseOutputStream.write(("Content-Type:image/jpeg").getBytes());
        responseOutputStream.write(("\r\n").getBytes());
        responseOutputStream.write(("Content-Length:" + data.length).getBytes());
        responseOutputStream.write(("\r\n").getBytes());
        responseOutputStream.write(("\r\n").getBytes());
        responseOutputStream.write(data);
        responseOutputStream.write(b);
        responseOutputStream.flush();
    }

    public static String generateRandomStreamId(){
        return UUID.randomUUID().toString();
    }
}
