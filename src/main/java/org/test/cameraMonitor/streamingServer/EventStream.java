package org.test.cameraMonitor.streamingServer;

import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.Image;
import org.test.cameraMonitor.entities.RecordedImage;
import org.test.cameraMonitor.util.APIUtils;
import org.test.cameraMonitor.util.EventStreamingData;
import org.test.cameraMonitor.util.EventUtils;
import org.test.cameraMonitor.util.ImageUtils;
import org.test.cameraMonitor.websocket.StreamingHandlerEngine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Ian
 * Date: 18/05/2013
 * Time: 18:42
 * To change this template use File | Settings | File Templates.
 */
public class EventStream implements Runnable{

    private Event event;
    private List<RecordedImage> eventImages;
    private int currentFrame;
    private int totalFrames;
    private Status status;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private boolean running = true;
    private StreamingHandlerEngine engine;
    private boolean initalPacketSent = false;


    public EventStream(Event event) throws IOException {
        this.status = Status.LOADING;
        this.event = event;
        this.eventImages = this.event.getStream();
        this.totalFrames = this.eventImages.size();
        this.status = Status.READY;
        //this.run();
    }

    public void update(){
        if (this.engine != null){
            this.engine.update(StreamingUtils.getJSONFromEventStreamingData(this));
        }
    }

    public void setEngine(StreamingHandlerEngine e){
        this.engine = e;
    }



    private void sendInitialPacket() throws IOException{
        String boundry = "--myboundary";
        String empty = "\r\n";
        byte[] b = boundry.getBytes();
        //FileInputStream fileInputStream = new FileInputStream(pdfFile);
        OutputStream responseOutputStream = response.getOutputStream();
        response.setContentType("multipart/x-mixed-replace; boundary=--myboundary");
        responseOutputStream.flush();
        this.initalPacketSent = true;
    }

    public void run () {
        while(running){
            System.out.println(this.event.getID() + "->" + this.status);
            try{
                while (this.status == Status.RUNNING && this.response != null){
                    if (!this.initalPacketSent){
                        this.sendInitialPacket();
                        this.initalPacketSent = true;
                    }
                    RecordedImage image = null;
                    while (this.status == Status.RUNNING) {
                        Image i = this.eventImages.get(this.currentFrame);
                        byte[] imageData = ImageUtils.putTimpStampOnImage(i, APIUtils.getTimestampFromLong(i.getDate())).getImageData();
                        StreamingUtils.sendMJPEGFrame(this.response.getOutputStream(), imageData);
                        this.currentFrame ++;
                        try {
                            Thread.sleep(1000 / Integer.parseInt(GlobalAttributes.getInstance().getConfigValue("FramesPerSecond")));
                        } catch (InterruptedException e) {
                            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
                        if (this.currentFrame == this.totalFrames){
                            this.status = Status.FINISHED;
                        }
                        this.update();
                    }
                }
            }
            catch (IOException e){
                this.update();
                running = false;
            }
        }
    }

    public int getCurrentFrame() {
        return currentFrame;
    }

    public int getTotalFrames() {
        return totalFrames;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status s){
        this.status = s;
        this.update();
    }

    public void setCurrentFrame(int f){
        this.currentFrame = f;
        this.update();
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }

    public boolean isRunning(){
        return this.running;
    }

    public void rwStream() {
    }

    public void ffStream() {

    }
}
