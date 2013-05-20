package org.test.cameraMonitor.websocket;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.streamingServer.EventStream;
import org.test.cameraMonitor.streamingServer.Status;
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
    private EventStream stream;


    public StreamingHandlerEngine(StreamingHandlerServlet.StreamHandlerMessageInbound chatMessageInbound, String id) {
        this.servlet = chatMessageInbound;
        GlobalAttributes.getInstance().getEventStreamTable().get(id).setEngine(this);
        this.stream = GlobalAttributes.getInstance().getEventStreamTable().get(id);
    }

    public void update(JSONObject response) {
        this.servlet.broadcast(response.toJSONString());
    }

    public void handleClientUpdate(CharBuffer message) {
        String jsonString = message.toString();
        JSONObject json = (JSONObject) JSONValue.parse(jsonString);
        String msg = (String)json.get("action");
        if (msg.equalsIgnoreCase("socketOpen")){
            this.stream.setStatus(Status.READY);
        }
        else if (msg.equalsIgnoreCase("startRunning")){
            this.stream.setStatus(Status.RUNNING);
        }
        else if (msg.equalsIgnoreCase("pauseStream")){
            this.stream.setStatus(Status.PAUSED);
        }
        else if (msg.equalsIgnoreCase("playStream")){
            this.stream.setStatus(Status.RUNNING);
        }
        else if (msg.equalsIgnoreCase("ffStream")){
            this.stream.ffStream();
        }
        else if (msg.equalsIgnoreCase("rwStream")){
            this.stream.rwStream();
        }
        else if (msg.equalsIgnoreCase("jumpTo")){
            int frameValue = 0;
            String type = (String)json.get("type");
            double value = Double.parseDouble((String)json.get("value"));
            if (type.equalsIgnoreCase("frame")){
                frameValue = (int)value;
            }
            else{
                frameValue =  (int)((double)value * (double)this.stream.getTotalFrames())/100;
            }
            this.stream.setCurrentFrame(frameValue);
        }
        this.stream.update();
    }
}
