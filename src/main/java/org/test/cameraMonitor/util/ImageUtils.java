package org.test.cameraMonitor.util;

import org.hibernate.Transaction;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.EventImage;
import org.test.cameraMonitor.entities.Image;
import org.test.cameraMonitor.entities.RecordedImage;
import org.test.cameraMonitor.recordingEngine.ImageCompare;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 16/03/2013
 * Time: 19:42
 * To change this template use File | Settings | File Templates.
 */
public class ImageUtils {

    public static Image putTimpStampOnImage(Image image, String timeStamp) throws IOException {
        BufferedImage bi = ImageUtils.getBIFromImage(image);
        Graphics2D graphics = bi.createGraphics();
        Font font = new Font("ARIAL", Font.BOLD, 20);
        graphics.setFont(font);
        graphics.setColor(Color.BLACK);
        graphics.drawString(String.valueOf(timeStamp), 400, 450);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write( bi, "jpg", baos );
        baos.flush();
        byte[] imageInByte = baos.toByteArray();
        image.setImageData(imageInByte);
        return image;
    }

    public static Image putTimpStampOnImage(byte[] image, String timeStamp) throws IOException {
        InputStream in = new ByteArrayInputStream(image);
        BufferedImage bi = ImageUtils.getBIFromByteArray(image);
        Graphics2D graphics = bi.createGraphics();
        if (bi != null){
            Font font = new Font("ARIAL", Font.BOLD, 20);
            graphics.setFont(font);
            graphics.setColor(Color.BLACK);
            graphics.drawString(String.valueOf(timeStamp), 400, 450);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write( bi, "jpg", baos );
            baos.flush();
            byte[] imageInByte = baos.toByteArray();
            Image returnImage = new RecordedImage(imageInByte);
            return returnImage;
        }
        return null;
    }

    public static BufferedImage getBIFromByteArray(byte[] input) throws IOException{
        return ImageIO.read(new ByteArrayInputStream(input));
    }

    public static BufferedImage getBIFromImage(Image image) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(image.getImageData()));
    }

    public static String getTimeStringFromLong(long dateTime){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(new Date(dateTime));
    }

    public static ImageCompare doesImageShowChange(Image currentImage, Image compareImage) throws IOException{
        if (currentImage == null | compareImage == null){
            return null;
        }
        BufferedImage newImage = ImageUtils.getBIFromImage(currentImage);
        BufferedImage oldImage = ImageUtils.getBIFromImage(compareImage);
        ImageCompare ic = new ImageCompare(oldImage, newImage);
        ic.setParameters(12, 8, 5, 10);
        ic.setDebugMode(0);
        ic.compare();
        //System.out.println(ic.match() + " - " + System.currentTimeMillis());
        if (!ic.match()){
            return ic;
        }
        return null;
    }

    public static void saveEventImage(ImageCompare ic) throws IOException {
        //System.out.println("Saving Event Image");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(ic.getChangeIndicator(), "jpg", outputStream);
        byte[] imageBytes = outputStream.toByteArray();
        EventImage eImg = new EventImage();
        eImg.setDate(System.currentTimeMillis());
        eImg.setImageData(imageBytes);
        org.test.cameraMonitor.entities.Event event = GlobalAttributes.getInstance().getCurrentEvent();
        event.getEventImages().add(eImg);
        eImg.setEvent(event);
        Transaction tx = null;
        org.hibernate.Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        tx = session.beginTransaction();
        session.saveOrUpdate(event);
        session.save(eImg);
        tx.commit();
//        session.close();
    }

    public static boolean shouldSaveImage(RecordedImage image) {
        GlobalAttributes globalAttributes = GlobalAttributes.getInstance();
        if (globalAttributes.isEventTriggered()){
            return true;
        }
        if (image.getCamera().saveAllImages() | globalAttributes.saveAllImages()){
            return true;
        }
        return true;
    }

    public static long getThreadSleepTime(RecordedImage image) {
        if (GlobalAttributes.getInstance().isEventTriggered()){
            return 67;
        }
        return 1000;
    }
}
