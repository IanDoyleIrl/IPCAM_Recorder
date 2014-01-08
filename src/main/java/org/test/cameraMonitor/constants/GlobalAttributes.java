package org.test.cameraMonitor.constants;

import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.EventImage;
import org.test.cameraMonitor.entities.RecordedImage;
import org.test.cameraMonitor.remoteStorage.AWS_S3StorageManager;
import org.test.cameraMonitor.websocket.cameraStream.CameraEndpoint;
import org.test.cameraMonitor.websocket.cameraStream.EventEndpoint;

import java.util.HashMap;
import java.util.Map;
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
    private AWS_S3StorageManager s3StorageManager;
    private HashMap<String, String> configValues = new HashMap<String, String>();
    private HashMap<Integer, RecordedImage> latestImages = new HashMap<Integer, RecordedImage>();
    private EventImage latestEventImage;
    private ConcurrentLinkedQueue<EventImage> S3Queue = new ConcurrentLinkedQueue<EventImage>();
    private ConcurrentLinkedQueue<Event> emailQueue = new ConcurrentLinkedQueue<Event>();
    private boolean eventTriggered = false;
    private Event currentEvent = null;
    private int eventTimeout = 120;
    private long eventTimestamp = System.currentTimeMillis();
    private CameraEndpoint cameraEndpoint;
    private EventEndpoint eventEndoint;

    private GlobalAttributes() {
        try {
            Properties prop = new Properties();
            prop.load(GlobalAttributes.class.getClassLoader().getResourceAsStream("config.properties"));
            configValues.putAll((Map)prop);
        }
        catch (Exception e){
            System.out.println(e);
        }

    }

    public synchronized static GlobalAttributes getInstance() {
        return ourInstance;
    }

    public String getConfigValue(String name) {
        return configValues.get(name);
    }

    public Event getCurrentEvent() {
        return currentEvent;
    }

    public void setCurrentEvent(Event currentEvent) {
        this.currentEvent = currentEvent;
    }

    public ConcurrentLinkedQueue<EventImage> getS3Queue() {
        return S3Queue;
    }

    public ConcurrentLinkedQueue<Event> getEmailQueue() {
        return emailQueue;
    }

    public boolean isEventTriggered() {
        //System.out.println(this.eventTriggered);
        return eventTriggered;
    }

    public void setEventTriggered(boolean eventTriggered) {
        this.eventTriggered = eventTriggered;
    }

    public void resetEventTimestamp() {
        this.eventTimestamp = System.currentTimeMillis();
    }

    public long getEventTimestamp() {
        return eventTimestamp;
    }

    public void setEventTimestamp(long eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    public void setS3StorageManager(AWS_S3StorageManager manager){
        this.s3StorageManager = manager;
    }

    public AWS_S3StorageManager getS3StorageManager(){
        return this.s3StorageManager;
    }

    public void notifyAllListeners(RecordedImage rImg) {

    }

    public HashMap<Integer, RecordedImage> getLatestImages() {
        return latestImages;
    }

    public void updateLatestCameraImage(Integer camera, RecordedImage image){
        this.latestImages.put(camera, image);
    }

    public EventImage getLatestEventImage(){
        return this.latestEventImage;
    }

    public boolean saveAllImages() {
        return false;
    }

    public CameraEndpoint getCameraEndpoint() {
        return cameraEndpoint;
    }

    public void setCameraEndpoint(CameraEndpoint cameraEndpoint) {
        this.cameraEndpoint = cameraEndpoint;
    }

    public int getEventTimeout() {
        return eventTimeout;
    }

    public EventEndpoint getEventEndoint() {
        return eventEndoint;
    }

    public void setEventEndoint(EventEndpoint eventEndoint) {
        this.eventEndoint = eventEndoint;
    }
}


