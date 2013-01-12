package org.test.cameraMonitor.recordingEngine;

import net.sf.jipcam.axis.MjpegFrame;
import org.apache.commons.lang.time.DateFormatUtils;
import org.test.cameraMonitor.entities.Camera;
import org.test.cameraMonitor.entities.EventImage;
import org.test.cameraMonitor.entities.RecordedImage;
import org.test.cameraMonitor.util.Startup;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
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

    private BufferedImage originalCompareImage = null;
    private int compareCount = 0;

    @Override
    public void run(){
        try{
            HttpURLConnection connection;
            Camera camera = Startup.getCamera();
            URL cam = new URL(camera.getUrl());
            connection = (HttpURLConnection)cam.openConnection();
            System.out.println(connection.getContentType());
            IPCameraTest in = new IPCameraTest(connection.getInputStream());
            MjpegFrame frame = null;
            while ((frame = in.readMjpegFrame()) != null) {
                createAndSaveNewRecordedImage(frame, camera);
            }
        } catch (EOFException eof) {
            eof.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAndSaveNewRecordedImage(MjpegFrame frame, Camera camera) throws IOException {
        String dateTime = DateFormatUtils.format(new Date().getTime(), "HH:MM:ss:SSSS");
        byte[] tempImage = (frame.getJpegBytes());
        RecordedImage rImg = new RecordedImage();
        //rImg.setCamera(camera);
        rImg.setDate(System.currentTimeMillis());
        rImg.setImageData(tempImage);
        rImg.save();
        compareCount ++;
        if (compareCount > 25){
            if (originalCompareImage != null){
                this.comparePreviousImageWithLatest(tempImage);
            }
            originalCompareImage = ImageIO.read(new ByteArrayInputStream(tempImage));
            compareCount = 0;
        }
    }

    private void comparePreviousImageWithLatest(byte[] tempImage) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(tempImage);
        BufferedImage bImageFromConvert = ImageIO.read(inputStream);
        ImageCompare ic = new ImageCompare(originalCompareImage, ImageIO.read(new ByteArrayInputStream(tempImage)));
        ic.setParameters(12, 8, 5, 10);
        ic.setDebugMode(0);
        ic.compare();
        System.out.println(ic.match());
        if (!ic.match()){
            System.out.println("Event Triggered");
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(ic.getChangeIndicator(), "jpg", outputStream);
            byte[] imageBytes = outputStream.toByteArray();
            EventImage eImg = new EventImage();
            eImg.setDate(System.currentTimeMillis());
            eImg.setImageData(imageBytes);
            eImg.save();
        }
    }

}
