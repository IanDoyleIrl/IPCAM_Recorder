package org.test.cameraMonitor.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.test.cameraMonitor.entities.Camera;
import org.test.cameraMonitor.entities.Event;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 17/01/2013
 * Time: 23:35
 * To change this template use File | Settings | File Templates.
 */
public class CameraUtil {

    public static JSONObject getCameraJSON(Camera camera){
        JSONObject response = new JSONObject();
        response.put("id", camera.getID());
        response.put("url", camera.getUrl());
        JSONArray events = new JSONArray();
        Iterator<Event> iterator = camera.getEvents().iterator();
        while (iterator.hasNext()){
            events.add(EventUtils.createEventJSON(iterator.next()));
        }
        response.put("events", events);
        return response;
    }

}
