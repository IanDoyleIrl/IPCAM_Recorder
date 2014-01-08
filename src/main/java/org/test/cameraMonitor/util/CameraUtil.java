package org.test.cameraMonitor.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.constants.eventTypes.CAMERA_UPDATE_TYPE;
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

    public static JSONObject getCameraJSON(Camera camera, boolean withEvents, boolean showConnectionStatus){
        JSONObject response = new JSONObject();
        response.put("type", "camera");
        response.put("id", camera.getID());
        response.put("name", camera.getName());
        response.put("url", camera.getUrl());
        response.put("active", camera.isActive());
        if (showConnectionStatus){
            JSONObject contactableObj = new JSONObject();
            Exception contactable = camera.isContactable();
            if (contactable != null){
                contactableObj.put("result", false);
                contactableObj.put("message", contactable.getMessage());
            }
            else{
                contactableObj.put("result", true);
            }
            response.put("contactable", contactableObj);
        }
        if (withEvents){
            JSONArray events = new JSONArray();
            Iterator<Event> iterator = camera.getEvents().iterator();
            while (iterator.hasNext()){
                events.add(EventUtils.createEventJSON(iterator.next()));
            }
            response.put("events", events);
        }
        return response;
    }

    public static JSONObject getNewRecordedImageForCameraJSON(Camera camera){
        JSONObject response = new JSONObject();
        response.put("type", CAMERA_UPDATE_TYPE.NEW_IMAGE.toString());
        response.put("id", camera.getID());
        response.put("imageTime", GlobalAttributes.getInstance().getLatestImages().get(camera.getID()).getDate());
        response.put("imageSize", GlobalAttributes.getInstance().getLatestImages().get(camera.getID()).getImageData().length);
        return response;
    }

}
