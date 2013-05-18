package org.test.cameraMonitor.util;

import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.websocket.StreamingHandlerEngine;

/**
 * Created with IntelliJ IDEA.
 * User: Ian
 * Date: 15/05/2013
 * Time: 22:35
 * To change this template use File | Settings | File Templates.
 */
public class EventStreamingData {

    private final Event event;
    int totalFrameCount;
    int currentFrameCount;
    boolean readyToStream = false;
    StreamingHandlerEngine engine;

    public EventStreamingData(Event e){
        this.event = e;
    }

    public Event getEvent() {
        return event;
    }

    public int getTotalFrameCount() {
        return totalFrameCount;
    }

    public void setTotalFrameCount(int totalFrameCount) {
        this.totalFrameCount = totalFrameCount;
    }

    public int getCurrentFrameCount() {
        return currentFrameCount;
    }

    public void setCurrentFrameCount(int currentFrameCount) {
        this.currentFrameCount = currentFrameCount;
    }

    public boolean isReadyToStream() {
        return readyToStream;
    }

    public void setReadyToStream(boolean readyToStream) {
        this.readyToStream = readyToStream;
    }

    public StreamingHandlerEngine getEngine() {
        return engine;
    }

    public void setEngine(StreamingHandlerEngine engine) {
        this.engine = engine;
    }

    public void update(){
        this.engine.update(EventUtils.getJSONFromEventStreamingData(this));
    }
}
