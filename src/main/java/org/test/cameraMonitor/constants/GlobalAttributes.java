package org.test.cameraMonitor.constants;

import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.EventImage;

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
    private HashMap<String, String> configValues = new HashMap<String, String>();
    private ConcurrentLinkedQueue<EventImage> S3Queue = new ConcurrentLinkedQueue<EventImage>();
    private ConcurrentLinkedQueue<Event> emailQueue = new ConcurrentLinkedQueue<Event>();
    private boolean eventTriggered = false;
    private Event currentEvent = null;
    private long eventTimestamp = System.currentTimeMillis();

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
}
