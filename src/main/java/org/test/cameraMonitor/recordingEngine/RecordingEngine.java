package org.test.cameraMonitor.recordingEngine;

import net.sf.jipcam.axis.MjpegFrame;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.constants.eventTypes.CAMERA_UPDATE_TYPE;
import org.test.cameraMonitor.entities.Camera;
import org.test.cameraMonitor.entities.RecordedImage;
import org.test.cameraMonitor.util.ConnectionUtils;
import org.test.cameraMonitor.util.EventUtils;
import org.test.cameraMonitor.util.ImageUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 10/01/2013
 * Time: 23:13
 * To change this template use File | Settings | File Templates.
 */
public class RecordingEngine implements Runnable {

    private Camera camera;
    private boolean running = true;

    public RecordingEngine(Camera camera){
        this.camera = camera;
    }

    public void shutdownThread(){
        this.running = false;
    }

    public HttpURLConnection getCameraConnection() throws IOException, InterruptedException {
        HttpURLConnection connection;
        URL cam = new URL(camera.getUrl());
        connection = (HttpURLConnection)cam.openConnection();
        return connection;
    }

    @Override
    public void run(){
        try {
            RecordedImage previousImage = null;
            RecordedImage image = null;
            HttpURLConnection conn = this.getCameraConnection();
            IPCameraTest in = new IPCameraTest(conn.getInputStream());
            if (camera != null){
                while (running){
                    System.out.println("Image Record @ " + new Date(System.currentTimeMillis()).toGMTString());
                    MjpegFrame frame = in.readMjpegFrame();
                    image = this.getImageFromCamera(frame);
                    GlobalAttributes.getInstance().getLatestImages().put(camera.getID(), image);
                    ImageCompare ic = ImageUtils.doesImageShowChange(image, previousImage);
                    if (ic != null){
                        //System.out.println("Match: " + image.getImageData().length);
                        if (!GlobalAttributes.getInstance().isEventTriggered()){
                            EventUtils.triggerEvent(image);
                            System.out.println("Event ID: " + GlobalAttributes.getInstance().getCurrentEvent().getID() + " - Started");
                        }
                        else{
                            System.out.println("Event ID: " + GlobalAttributes.getInstance().getCurrentEvent().getID() + " - Time Reset");
                            EventUtils.resetEventTimeRemaining();
                        }
                        ImageUtils.saveEventImage(ic);
                    }
                    if (GlobalAttributes.getInstance().isEventTriggered()){
                        if (EventUtils.getEventTimeRemaining() <= 0){
                            System.out.println("Event ID: " + GlobalAttributes.getInstance().getCurrentEvent().getID() + " - Stopped");
                            EventUtils.stopEvent();
                        }
                    }
                    if (ImageUtils.shouldSaveImage(image)){
                            image.save();
                    }
                    previousImage = image;
                    GlobalAttributes.getInstance().updateLatestCameraImage(camera.getID(), image);
                    System.out.println("Latest Image For Camera " + camera.getID() + " updated @ " + System.currentTimeMillis());
                    ConnectionUtils.notifyAllCameraListeners(camera, CAMERA_UPDATE_TYPE.NEW_IMAGE);
                    Thread.sleep(ImageUtils.getThreadSleepTime(image));
                }
            }
        }
        catch (InterruptedException innExp){
            System.out.println("IOException with camera: " + this.camera.getID());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Thread.sleep(60000);
            } catch (InterruptedException e) {
                System.out.println("Thread exception with camera: " + this.camera.getID());
            }
        }
    }

    private RecordedImage getImageFromCamera(MjpegFrame frame) {
        byte[] tempImage = (frame.getJpegBytes());
        RecordedImage rImg = new RecordedImage();
        rImg.setCamera(camera);
        rImg.setDate(System.currentTimeMillis());
        rImg.setImageData(tempImage);
        return rImg;
    }

}
