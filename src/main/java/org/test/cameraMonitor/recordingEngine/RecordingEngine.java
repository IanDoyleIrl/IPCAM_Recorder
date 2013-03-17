package org.test.cameraMonitor.recordingEngine;

import net.sf.jipcam.axis.MjpegFrame;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.test.cameraMonitor.constants.EventType;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.Camera;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.EventImage;
import org.test.cameraMonitor.entities.RecordedImage;
import org.test.cameraMonitor.util.DatabaseUtils;
import org.test.cameraMonitor.util.HibernateUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
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
//            Properties properties = Properti
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
                createAndSaveNewRecordedImage(frame, camera);
                logger.info("sleeping.....");
                Thread.sleep(1000 / framesPerSeconds);
            }
        } catch (EOFException eof) {
            eof.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] putTimpStampOnImage(RecordedImage i, BufferedImage bi) throws IOException {
        Graphics2D graphics = bi.createGraphics();
        Font font = new Font("ARIAL", Font.PLAIN, 20);
        graphics.setFont(font);
        graphics.drawString(String.valueOf(i.getDate()), 50, 50);
        bi.flush();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.createImageOutputStream(out);
        return out.toByteArray();
    }

    private void createAndSaveNewRecordedImage(MjpegFrame frame, Camera camera) throws IOException {
        System.out.println("createAndSaveNewRecordedImage() frameLength: " + frame.getBytes().length + ", camera: " + camera.getID());
        String dateTime = DateFormatUtils.format(new Date().getTime(), "HH:MM:ss:SSSS");
        RecordedImage rImg = new RecordedImage();
        byte[] imageData = frame.getBytes();
        //rImg.setCamera(camera);
        rImg.setDate(System.currentTimeMillis());
        rImg.setImageData(frame.getBytes());
        rImg.save();
        compareCount ++;
        if (global.isEventTriggered() == true){
            global.setEventTimestamp(System.currentTimeMillis());
        }
        if (compareCount > 10){
            if (originalCompareImage != null){
                this.comparePreviousImageWithLatest(imageData);
            }
            originalCompareImage = ImageIO.read(new ByteArrayInputStream(imageData));
            compareCount = 0;
            global.resetEventTimestamp();
        }
        long diff = ((System.currentTimeMillis() - global.getEventTimestamp()) / 1000);
        if (global.getCurrentEvent() != null &
                ((System.currentTimeMillis() - global.getEventTimestamp()) / 1000) >= Integer.parseInt(global.getConfigValue("EventTimeout"))){
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

    public String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd-MM-yyyy - HH:mm:ss");
        return format.format(date).toString();
    }

    private void comparePreviousImageWithLatest(byte[] tempImage) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(tempImage);
        BufferedImage bImageFromConvert = ImageIO.read(inputStream);
        ImageCompare ic = new ImageCompare(originalCompareImage, ImageIO.read(new ByteArrayInputStream(tempImage)));
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
                event.setName(convertTime(System.currentTimeMillis()));
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
            //session.close();
        }
    }

}
