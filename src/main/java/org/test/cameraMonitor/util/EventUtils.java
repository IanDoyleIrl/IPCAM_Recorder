package org.test.cameraMonitor.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.EventImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
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

    public static JSONObject createEventJSON(Event event){
        JSONObject response = new JSONObject();
        if (event != null){
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
            Event e = (Event) GlobalAttributes.getInstance().getAttributes().get("currentEvent");
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

    public static JSONObject createEventStatisticsJSON() {
        int totalEventCount = ((Long) HibernateUtil.getSessionFactory().openSession().createQuery("SELECT COUNT (id) FROM Event").uniqueResult()).intValue();
        int totalEventImageCount = ((Long)HibernateUtil.getSessionFactory().openSession().createQuery("SELECT COUNT (id) FROM EventImage").uniqueResult()).intValue();
        long averageImagesPerEvent = 0;
        if (totalEventCount > 0 && totalEventImageCount > 0){
            averageImagesPerEvent = totalEventCount / totalEventImageCount;
        }
        Event activeEvent = (Event)GlobalAttributes.getInstance().getAttributes().get("currentEvent");
        long currentActiveEventId = 0;
        if (activeEvent != null){
            currentActiveEventId = activeEvent.getID();
        }
        JSONObject response = new JSONObject();
        response.put("totalEventCount", totalEventCount);
        response.put("totalEventImageCount", totalEventImageCount);
        response.put("currentActiveEventId", currentActiveEventId);
        response.put("isEventActive", (Boolean)GlobalAttributes.getInstance().getAttributes().get("eventTriggered"));
        response.put("averageImagesPerEvent", averageImagesPerEvent);
        response.put("eventFrameCountRemaining", GlobalAttributes.getInstance().getEventFrameCount());
        return response;
    }
}
