package org.test.cameraMonitor.util;

import org.test.cameraMonitor.entities.Image;
import org.test.cameraMonitor.entities.RecordedImage;

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

}
