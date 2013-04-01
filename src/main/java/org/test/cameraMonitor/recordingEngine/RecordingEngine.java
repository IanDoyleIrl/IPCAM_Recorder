package org.test.cameraMonitor.recordingEngine;

import net.sf.jipcam.axis.MjpegFrame;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.test.cameraMonitor.constants.EventType;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.*;
import org.test.cameraMonitor.util.DatabaseUtils;
import org.test.cameraMonitor.util.EventUtils;
import org.test.cameraMonitor.util.HibernateUtil;
import org.test.cameraMonitor.util.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 10/01/2013
 * Time: 23:13
 * To change this template use File | Settings | File Templates.
 */
public class RecordingEngine implements Runnable {

    private Image compareImage = null;
    private int compareCount = 0;
    private int eventCount = 0;
    private GlobalAttributes global = null;
    private Camera camera;
    private String s =  GlobalAttributes.getInstance().getConfigValue("FramesPerSecond");
    private int framesPerSeconds;
    private boolean running = true;
    private long lastCheckTime = System.currentTimeMillis();

    Logger logger = LogManager.getLogger(RecordingEngine.class.getName());

    public RecordingEngine(Camera camera){
        this.camera = camera;
        try{
            this.framesPerSeconds = Integer.parseInt(s);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void shutdownThread(){
        this.running = false;
    }

    @Override
    public void run(){
        try{
            DatabaseUtils.cleanUpEventRecords();
            logger.info("Starting run()");
            logger.error("error");
            logger.trace("trace");
            global = GlobalAttributes.getInstance();
            //global.getAttributes().put("eventTriggered", false);
            HttpURLConnection connection;
            URL cam = new URL(camera.getUrl());
            connection = (HttpURLConnection)cam.openConnection();
            System.out.println(connection.getContentType());
            IPCameraTest in = new IPCameraTest(connection.getInputStream());
            while (running) {
                MjpegFrame frame = in.readMjpegFrame();
                //createAndSaveNewRecordedImage(frame, camera);
                logger.info("sleeping.....");
                Thread.sleep(1000 / framesPerSeconds);
            }
        } catch (EOFException eof) {
            eof.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAndSaveNewRecordedImage(MjpegFrame frame, Camera camera) throws IOException {
        //System.out.println("createAndSaveNewRecordedImage() frameLength: " + frame.getBytes().length + ", camera: " + camera.getID());
        byte[] tempImage = (frame.getJpegBytes());
        RecordedImage rImg = new RecordedImage();
        //rImg.setCamera(camera);
        rImg.setDate(System.currentTimeMillis());
        rImg.setImageData(tempImage);
        rImg.save();
        compareCount ++;
        if (compareCount > 10){
            if (compareImage != null){
                this.comparePreviousImageWithLatest(rImg);
            }
            compareImage = rImg;
            compareCount = 0;
            //global.resetEventTimestamp();
        }
        long currentTime = System.currentTimeMillis();
        long timeStamp = global.getEventTimestamp();
        long diff = (currentTime - timeStamp) / 1000;
        int timeout = Integer.parseInt(global.getConfigValue("EventTimeout"));
        System.out.println("Diff: " + diff + ", currentTime: " + System.currentTimeMillis() + ", eventTime: " + timeStamp);
        Event currentEvent = global.getCurrentEvent();
        if (currentEvent != null & diff >= timeout){
            Transaction tx = null;
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            tx = session.beginTransaction();
            Event event = global.getCurrentEvent();
            event.setTimeEnded(System.currentTimeMillis());
            event.setEventType(EventType.UNSURE);
            session.saveOrUpdate(event);
            tx.commit();
            global.setCurrentEvent(null);
            global.setEventTriggered(false);
            global.resetEventTimestamp();
        }
    }

    private void comparePreviousImageWithLatest(Image currentImage) throws IOException {
        BufferedImage newImage = ImageUtils.getBIFromImage(currentImage);
        BufferedImage oldImage = ImageUtils.getBIFromImage(compareImage);
        ImageCompare ic = new ImageCompare(oldImage, newImage);
        ic.setParameters(12, 8, 5, 10);
        ic.setDebugMode(0);
        ic.compare();
        System.out.println(ic.match() + " - " + System.currentTimeMillis());
        if (!ic.match()){
            Transaction tx = null;
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            tx = session.beginTransaction();
            //session.save(this);
            //tx.commit();
            //session.close();
            boolean eventTriggered = global.isEventTriggered();
            if (!eventTriggered){
                Event event = new Event();
                event.setTimeStarted(System.currentTimeMillis());
                event.setName(EventUtils.convertTime(System.currentTimeMillis()));
                event.setCamera(camera);
                event.setComments("Some comments go here");
                session.save(event);
                global.setCurrentEvent(event);
                global.setEventTriggered(true);
                if (Boolean.parseBoolean(global.getConfigValue("SendToEmail"))){
                    global.getEmailQueue().add(event);
                }
            }
            //System.out.println("Event Triggered");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(ic.getChangeIndicator(), "jpg", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            EventImage eImg = new EventImage();
            eImg.setDate(System.currentTimeMillis());
            eImg.setImageData(imageBytes);
            Event event = global.getCurrentEvent();
            event.getEventImages().add(eImg);
            eImg.setEvent(event);
            session.saveOrUpdate(event);
            session.save(eImg);
            tx.commit();
            if (Boolean.parseBoolean(global.getConfigValue("SendToS3"))){
                global.getS3Queue().add(eImg);
            }
            global.resetEventTimestamp();
            //session.close();
        }
    }

}
