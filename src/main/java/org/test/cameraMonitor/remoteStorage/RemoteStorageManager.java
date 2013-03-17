package org.test.cameraMonitor.remoteStorage;

import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.EventImage;
import org.test.cameraMonitor.entities.Image;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 26/01/2013
 * Time: 17:06
 * To change this template use File | Settings | File Templates.
 */
public interface RemoteStorageManager extends Runnable {

    public void uploadImageFromEvent(EventImage image);

    public boolean isEventImageSynced(EventImage image);

    public EventImage getRemoteCopyOfEventImage(EventImage image) throws IOException;

    public ArrayList<Image> getAllImagesByEvent(Event event);

    public void closeConnection();
}
