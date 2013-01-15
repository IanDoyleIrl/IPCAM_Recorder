package org.test.cameraMonitor.streamingServer;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.RecordedImage;
import org.test.cameraMonitor.entities.RecordedStream;
import org.test.cameraMonitor.util.HibernateUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
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
            if (image == null){
                image = iterator.next();
            }
            byte[] imageData = iterator.next().getImageData();
            StreamingUtils.sendMJPEGFrame(responseOutputStream, imageData);
            frameTime = image.getDate();
            image = iterator.next();
            try {
                Thread.sleep(GlobalAttributes.getInstance().getMJPEGSleepTime());
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
            if (image == null){
                image = iterator.next();
            }
            byte[] imageData = iterator.next().getImageData();
            StreamingUtils.sendMJPEGFrame(responseOutputStream, imageData);
            frameTime = image.getDate();
            image = iterator.next();
            try {
                Thread.sleep(image.getDate() - frameTime);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    public static void handleLiveStreaming(HttpServletResponse response, HttpServletRequest request) throws IOException {
        //response.setContentLength((int) pdfFile.length());
        //FileInputStream fileInputStream = new FileInputStream(pdfFile);
        OutputStream responseOutputStream = response.getOutputStream();
        response.setContentType("multipart/x-mixed-replace; boundary=--myboundary");
        responseOutputStream.flush();
        int bytes;
        while ((true)) {
            //responseOutputStream.write(b);
            DetachedCriteria maxQuery = DetachedCriteria.forClass( RecordedImage.class );
            maxQuery.setProjection( Projections.max("Id") );
            Criteria query = HibernateUtil.getSessionFactory().openSession().createCriteria( RecordedImage.class );
            query.add( Property.forName("Id").eq( maxQuery ) );
            RecordedImage image = (RecordedImage) query.uniqueResult();
            System.out.println("Sending mjpeg frame from id " + image.getId() + "sent");
            //response.addHeader("content-length", String.valueOf(image.getImageData().length));
            //response.addHeader("content-type", "image/jpeg");
            StreamingUtils.sendMJPEGFrame(responseOutputStream, image.getImageData());
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }


    private static void sendMJPEGFrame(OutputStream responseOutputStream, byte[] imageData) throws IOException{
        System.out.println("Length " + imageData.length + "sent");
        responseOutputStream.write(("--myboundary").getBytes());
        responseOutputStream.write(("\r\n").getBytes());
        responseOutputStream.write(("Content-Type:image/jpeg").getBytes());
        responseOutputStream.write(("\r\n").getBytes());
        responseOutputStream.write(("Content-Length:" + imageData.length).getBytes());
        responseOutputStream.write(("\r\n").getBytes());
        //responseOutputStream.write(empty.getBytes());
        //responseOutputStream.write(empty.getBytes());
        responseOutputStream.write(("\r\n").getBytes());
        responseOutputStream.write(imageData);
        responseOutputStream.write(b);
        responseOutputStream.flush();
        //To change body of created methods use File | Settings | File Templates.
    }
}
