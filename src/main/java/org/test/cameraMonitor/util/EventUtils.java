package org.test.cameraMonitor.util;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.constants.eventTypes.EVENT_UPDATE_TYPE;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.EventImage;
import org.test.cameraMonitor.entities.RecordedImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 13/01/2013
 * Time: 21:37
 * To change this template use File | Settings | File Templates.
 */
public class EventUtils {

    public static ZipOutputStream getZipFromEventImages(Set<EventImage> images){
        try{
            Event rootEvent = images.iterator().next().getEvent();
            ZipOutputStream zout = new ZipOutputStream(new ByteArrayOutputStream());
            for (EventImage image : images){
                zout.putNextEntry(new ZipEntry(String.valueOf(image.getDate())));
                zout.write(image.getImageData(), 0, image.getImageData().length);
                zout.closeEntry();
            }
            return zout;
        }
        catch (IOException e){
            return null;
        }
    }

    public static String convertTime(long time){
        Date date = new Date(time);
        Format format = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
        return format.format(date).toString();
    }


    public static JSONObject createEventJSON(Event event){
        JSONObject response = new JSONObject();
        if (event != null){
            response.put("type", "event");
            response.put("id", event.getID());
            response.put("timeStarted", event.getTimeStarted());
            if (event.getTimeEnded() != 0){
                response.put("timeEnded", System.currentTimeMillis());
            }
            response.put("comments", event.getComments());
            response.put("name", event.getName());
            JSONArray eventImages = new JSONArray();
            Iterator<EventImage> iter = event.getEventImages().iterator();
            while (iter.hasNext()){
                EventImage eventImage = iter.next();
                JSONObject image = new JSONObject();
                image.put("id", eventImage.getId());
                image.put("time", eventImage.getDate());
                eventImages.add(image);
            }
            Event e = (Event) GlobalAttributes.getInstance().getCurrentEvent();
            if (e != null){
                if (e.getID() == event.getID()){
                    response.put("active", true);
                }
            }
            else{
                response.put("active", false);
            }

            response.put("eventImages", eventImages);
        }
        return response;
    }


    public static void triggerEvent(RecordedImage image) throws IOException {
        Event event = new Event();
        event.setTimeStarted(System.currentTimeMillis());
        event.setName(EventUtils.convertTime(System.currentTimeMillis()));
        event.setCamera(image.getCamera());
        event.setComments("Some comments go here");
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction tx = session.beginTransaction();
        session.save(event);
        GlobalAttributes.getInstance().setEventTimestamp(System.currentTimeMillis());
        GlobalAttributes.getInstance().setCurrentEvent(event);
        GlobalAttributes.getInstance().setEventTriggered(true);
        session.close();
        ConnectionUtils.notifyAllEventListeners(GlobalAttributes.getInstance().getCurrentEvent(), EVENT_UPDATE_TYPE.STARTED);
    }

    public static int getEventTimeRemaining() {
        long diff = System.currentTimeMillis() - GlobalAttributes.getInstance().getEventTimestamp();
        int secondsDifference = Math.round(diff / 1000);
        System.out.println("Event ID: " + GlobalAttributes.getInstance().getCurrentEvent().getID() + " - " + (GlobalAttributes.getInstance().getEventTimeout() - secondsDifference) + " remaining");
        return GlobalAttributes.getInstance().getEventTimeout() - secondsDifference;
    }

    public static void stopEvent() throws IOException {
        Event event = GlobalAttributes.getInstance().getCurrentEvent();
        event.setTimeEnded(System.currentTimeMillis());
        GlobalAttributes.getInstance().setCurrentEvent(null);
        GlobalAttributes.getInstance().setEventTriggered(false);
        ConnectionUtils.notifyAllEventListeners(GlobalAttributes.getInstance().getCurrentEvent(), EVENT_UPDATE_TYPE.STOPPED);
    }

    public static void resetEventTimeRemaining() {
        GlobalAttributes.getInstance().setEventTimestamp(System.currentTimeMillis());
    }

    public static JSONObject getNewEventImageJSON(Event event) {
        JSONObject response = new JSONObject();
        response.put("eventId", event.getID());
        response.put("eventType", EVENT_UPDATE_TYPE.NEW_EVENT_IMAGE.toString());
        return response;
    }

    public static JSONObject getNewEventStartedJSON(Event event) {
        JSONObject response = new JSONObject();
        response.put("eventId", event.getID());
        response.put("eventType", EVENT_UPDATE_TYPE.STARTED.toString());
        return response;
    }

    public static JSONObject getEventStoppedJSON(Event event) {
        JSONObject response = new JSONObject();
        response.put("eventId", event.getID());
        response.put("eventType", EVENT_UPDATE_TYPE.STOPPED.toString());
        return response;
    }

    public static JSONObject getEventExtendedJSON(Event event) {
        JSONObject response = new JSONObject();
        response.put("eventId", event.getID());
        response.put("eventType", EVENT_UPDATE_TYPE.EXTENDED.toString());
        response.put("totalRemainingTime", EventUtils.getEventTimeRemaining());
        return response;
    }

    public static JSONObject getEventUpdatedJSON(Event event) {
        JSONObject response = new JSONObject();
        response.put("eventId", event.getID());
        response.put("eventType", EVENT_UPDATE_TYPE.NEW_EVENT_IMAGE.toString());
        return response;
    }
}
