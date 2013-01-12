package org.test.cameraMonitor.recordingEngine;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class StreamingEngine {

    private static final double FRAME_RATE = 50;
    private static final int SECONDS_TO_RUN_FOR = 20;
    private static final String outputFilename = "./test.mp4";
    private static Dimension screenBounds;

    public static void main(String[] args) {
        StreamingEngine engine = new StreamingEngine();
    }

    public StreamingEngine(){
        // let's make a IMediaWriter to write the file.
        final IMediaWriter writer = ToolFactory.makeWriter(outputFilename);
        screenBounds = new Dimension(640, 480);
        // We tell it we're going to add one video stream, with id 0,
        // at position 0, and that it will have a fixed frame rate of FRAME_RATE.
        writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MJPEG, screenBounds.width / 2, screenBounds.height / 2);
        long startTime = System.nanoTime();
        ArrayList<byte[]> images = ConnectionUtil.getAllImagesFromDatabaseAsList();
        Iterator<byte[]> iter = images.iterator();
        while (iter.hasNext()){
            BufferedImage screen = this.getBufferedImageFromByteArray(iter.next());
            // convert to the right image type
            BufferedImage bgrScreen = convertToType(screen, BufferedImage.TYPE_3BYTE_BGR);
            // encode the image to stream #0
            writer.encodeVideo(0, bgrScreen, System.nanoTime() - startTime, TimeUnit.NANOSECONDS);
            // sleep for frame rate milliseconds
        }
        writer.close();
    }

    public static BufferedImage convertToType(BufferedImage sourceImage, int targetType) {
        BufferedImage image;
        // if the source image is already the target type, return the source image
        if (sourceImage.getType() == targetType) {
            image = sourceImage;
        }
        // otherwise create a new image of the target type and draw the new image
        else {
            image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), targetType);
            image.getGraphics().drawImage(sourceImage, 0, 0, null);
        }
        return image;
    }

    private static BufferedImage getNextImageFromDatabase() {
        try {
            Robot robot = new Robot();
            Rectangle captureSize = new Rectangle(screenBounds);
            return robot.createScreenCapture(captureSize);
        } catch (AWTException e) {
            e.printStackTrace();
            return null;
        }
    }

    private BufferedImage getBufferedImageFromByteArray(byte[] imageInByte){
        try {
            // convert byte array back to BufferedImage
            InputStream in = new ByteArrayInputStream(imageInByte);
            BufferedImage bImageFromConvert = ImageIO.read(in);
            return bImageFromConvert;
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
