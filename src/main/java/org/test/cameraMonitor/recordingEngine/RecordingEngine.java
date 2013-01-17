package org.test.cameraMonitor.recordingEngine;

import net.sf.jipcam.axis.MjpegFrame;
import org.apache.commons.lang.time.DateFormatUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.test.cameraMonitor.constants.EventType;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.Camera;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.EventImage;
import org.test.cameraMonitor.entities.RecordedImage;
import org.test.cameraMonitor.util.HibernateUtil;
import org.test.cameraMonitor.util.Startup;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 10/01/2013
 * Time: 23:13
 * To change this template use File | Settings | File Templates.
 */
public class RecordingEngine implements Runnable {

    private BufferedImage originalCompareImage = null;
    private int compareCount = 0;
    private int eventCount = 0;
    private GlobalAttributes global = null;
    private Camera camera;

    @Override
    public void run(){
        try{
//            Properties properties = Properti
            global = GlobalAttributes.getInstance();
            global.getAttributes().put("eventTriggered", false);
            HttpURLConnection connection;
            camera = Startup.getCamera();
            URL cam = new URL(camera.getUrl());
            connection = (HttpURLConnection)cam.openConnection();
            System.out.println(connection.getContentType());
            IPCameraTest in = new IPCameraTest(connection.getInputStream());
            MjpegFrame frame = in.readMjpegFrame();
            while (frame != null) {
                createAndSaveNewRecordedImage(frame, camera);
                Thread.sleep(GlobalAttributes.getInstance().getMJPEGSleepTime());
            }
        } catch (EOFException eof) {
            eof.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAndSaveNewRecordedImage(MjpegFrame frame, Camera camera) throws IOException {
        String dateTime = DateFormatUtils.format(new Date().getTime(), "HH:MM:ss:SSSS");
        byte[] tempImage = (frame.getJpegBytes());
        RecordedImage rImg = new RecordedImage();
        //rImg.setCamera(camera);
        rImg.setDate(System.currentTimeMillis());
        rImg.setImageData(tempImage);
        rImg.save();
        compareCount ++;
        if ((Boolean)global.getAttributes().get("eventTriggered") == true){
            global.incrementEventFrameCount();
        }
        if (compareCount > 10){
            if (originalCompareImage != null){
                this.comparePreviousImageWithLatest(tempImage);
            }
            originalCompareImage = ImageIO.read(new ByteArrayInputStream(tempImage));
            compareCount = 0;
            global.getAttributes().put("eventFrameCount", 0);
        }
        if (global.getEventFrameCount() >= 150 & global.getAttributes().get("currentEvent") != null){
            Transaction tx = null;
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            tx = session.beginTransaction();
            Event event = (Event)global.getAttributes().get("currentEvent");
            event.setTimeEnded(System.currentTimeMillis());
            event.setEventType(EventType.UNSURE);
            session.saveOrUpdate(event);
            tx.commit();
            global.getAttributes().put("currentEvent", null);
            global.getAttributes().put("eventTriggered", false);
            global.resetEventFrameCount();
        }
    }

    private void comparePreviousImageWithLatest(byte[] tempImage) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(tempImage);
        BufferedImage bImageFromConvert = ImageIO.read(inputStream);
        ImageCompare ic = new ImageCompare(originalCompareImage, ImageIO.read(new ByteArrayInputStream(tempImage)));
        ic.setParameters(12, 8, 5, 10);
        ic.setDebugMode(0);
        ic.compare();
        System.out.println(ic.match());
        if (!ic.match()){
            Transaction tx = null;
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            tx = session.beginTransaction();
            //session.save(this);
            //tx.commit();
            //session.close();
            boolean eventTriggered = (Boolean)global.getAttributes().get("eventTriggered");
            if (!eventTriggered){
                Event event = new Event();
                event.setTimeStarted(System.currentTimeMillis());
                event.setName("TEMPNAME - " + System.currentTimeMillis());
                event.setCamera(camera);
                event.setComments("Some comments go here");
                session.save(event);
                global.getAttributes().put("eventTriggered", true);
                global.getAttributes().put("currentEvent", event);
            }
            //System.out.println("Event Triggered");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(ic.getChangeIndicator(), "jpg", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            EventImage eImg = new EventImage();
            eImg.setDate(System.currentTimeMillis());
            eImg.setImageData(imageBytes);
            Event event = (Event)global.getAttributes().get("currentEvent");
            event.getEventImages().add(eImg);
            eImg.setEvent(event);
            session.saveOrUpdate(event);
            session.save(eImg);
            tx.commit();
            //session.close();
        }
    }

}
