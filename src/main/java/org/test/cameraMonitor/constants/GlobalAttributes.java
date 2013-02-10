package org.test.cameraMonitor.constants;

import org.test.cameraMonitor.entities.Camera;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.EventImage;
import org.test.cameraMonitor.remoteStorage.RemoteStorageManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 13/01/2013
 * Time: 15:16
 * To change this template use File | Settings | File Templates.
 */
public class GlobalAttributes {
    private static GlobalAttributes ourInstance = new GlobalAttributes();
    private int eventFrameCount = 0;
    private int SleepTime = 20;
    private int STMPPort;
    private String SMTPUsername;
    private String SMTPPassword;
    private Camera defaultCamera;
    private ArrayList<String> emailAddresses = new ArrayList<String>();
    private Event currentEvent;
    private boolean eventTriggered;
    private ArrayList<RemoteStorageManager> remoteStorageManagerArrayList = new ArrayList<RemoteStorageManager>();
    private int eventFrameTimeout;
    private String S3AccessKey;
    private String S3RegionId;
    private String S3SecretKey;
    private String SMTPServer;
    private boolean sendToS3;
    private boolean sendEmail;
    private ConcurrentLinkedQueue<EventImage> S3Queue = new ConcurrentLinkedQueue<EventImage>();
    private ConcurrentLinkedQueue<Event> emailQueue = new ConcurrentLinkedQueue<Event>();

    public synchronized static GlobalAttributes getInstance() {
        return ourInstance;
    }

    private GlobalAttributes() {
        Properties prop = new Properties();
        try {
            prop.load(GlobalAttributes.class.getClassLoader().getResourceAsStream("config.properties"));
            this.eventFrameTimeout = Integer.parseInt(prop.getProperty("eventFrameTimeout"));
            this.sendEmail = Boolean.parseBoolean(prop.getProperty("sendEmail"));
            this.sendToS3 = Boolean.parseBoolean(prop.getProperty("sendToS3"));
            this.SleepTime = Integer.parseInt(prop.getProperty("sleepTime"));
            this.STMPPort = Integer.parseInt(prop.getProperty("smtpPort"));
            this.S3AccessKey = prop.getProperty("accessKey");
            this.S3SecretKey = prop.getProperty("secretKey");
            this.S3RegionId = prop.getProperty("regionID");
            this.defaultCamera = new Camera();
            this.defaultCamera.setActive(true);
            this.defaultCamera.setUrl(prop.getProperty("defaultCamera"));
            this.defaultCamera.setCanControl(false);
            this.defaultCamera.setName("Default Camera");
            this.SMTPPassword = prop.getProperty("smtpPassword");
            this.SMTPUsername = prop.getProperty("smtpUsername");
            this.SMTPServer = prop.getProperty("smtpServer");
            String emailAddressString = prop.getProperty("emailAddresses");
            String[] tempEmail = emailAddressString.split(";");
            for (String s : tempEmail){
                this.emailAddresses.add(s);
            }
            //if ((Long)HibernateUtil.getSessionFactory().openSession().createCriteria("Camera").setProjection(Projections.rowCount()).uniqueResult() == 0){
             //   HibernateUtil.getSessionFactory().openSession().save(defaultCamera);
            //};

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        };
    }

    public int getEventFrameCount(){
        return this.eventFrameCount;
    }

    public void incrementEventFrameCount(){
        this.eventFrameCount ++;
    }

    public void resetEventFrameCount(){
        this.eventFrameCount = 0;
    }

    public int getSleepTime() {
        return this.SleepTime;
    }

    public int getEventFrameTimeout() {
        return eventFrameTimeout;
    }

    public int getSTMPPort() {
        return STMPPort;
    }

    public String getSMTPUsername() {
        return SMTPUsername;
    }

    public String getSMTPPassword() {
        return SMTPPassword;
    }

    public Camera getDefaultCamera() {
        return defaultCamera;
    }

    public ArrayList<String> getEmailAddresses() {
        return emailAddresses;
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public boolean isEventTriggered() {
        return eventTriggered;
    }

    public ArrayList<RemoteStorageManager> getRemoteStorageManagerArrayList() {
        return remoteStorageManagerArrayList;
    }

    public String getS3AccessKey() {
        return S3AccessKey;
    }

    public String getS3RegionId() {
        return S3RegionId;
    }

    public String getS3SecretKey() {
        return S3SecretKey;
    }

    public void resetEventTriggered() {
        this.eventTriggered = false;
    }

    public void resetCurrentEvent() {
        this.currentEvent = null;
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    public void setEventTriggered(boolean value) {
        this.eventTriggered = value;
    }

    public boolean isSendToS3() {
        return sendToS3;
    }

    public boolean isSendEmail() {
        return sendEmail;
    }

    public ConcurrentLinkedQueue<EventImage> getS3Queue() {
        return S3Queue;
    }

    public ConcurrentLinkedQueue<Event> getEmailQueue() {
        return emailQueue;
    }

    public String getSMTPServer() {
        return SMTPServer;
    }
}
