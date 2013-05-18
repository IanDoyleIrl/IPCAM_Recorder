package org.test.cameraMonitor.websocket;

import org.json.simple.JSONObject;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.util.APIUtils;
import org.test.cameraMonitor.util.EventStreamingData;
import org.test.cameraMonitor.util.EventUtils;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 24/03/2013
 * Time: 23:11
 * To change this template use File | Settings | File Templates.
 */
public class StreamingHandlerEngine{

    private StreamingHandlerServlet.StreamHandlerMessageInbound servlet;
    private Event event;

    public StreamingHandlerEngine(StreamingHandlerServlet.StreamHandlerMessageInbound chatMessageInbound, Event event) {
        this.servlet = chatMessageInbound;
        this.event = event;
        java.util.Hashtable<Event, EventStreamingData> hm = GlobalAttributes.getInstance().getEventStreamingDataHashMap();
        EventStreamingData sd = hm.get(event);
        ArrayList<EventStreamingData> arr = new ArrayList<EventStreamingData>(hm.values());
        for (EventStreamingData d : arr){
            if (d.getEvent().getID() == event.getID()){
                d.setEngine(this);
            }
        }
    }

    public void update(JSONObject response) {
        this.servlet.broadcast(response.toJSONString());
    }
}
