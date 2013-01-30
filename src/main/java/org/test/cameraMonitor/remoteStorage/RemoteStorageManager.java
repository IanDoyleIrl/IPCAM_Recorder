package org.test.cameraMonitor.remoteStorage;

import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.EventImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 26/01/2013
 * Time: 17:06
 * To change this template use File | Settings | File Templates.
 */
public interface RemoteStorageManager extends Runnable {

    public SynchronousQueue<EventImage> queue = new SynchronousQueue<EventImage>();

    public void uploadImageFromEvent(EventImage image);

    public boolean isEventImageSynced(EventImage image);

    public EventImage getRemoteCopyOfEventImage(EventImage image) throws IOException;

    public ArrayList<EventImage> getAllImagesByEvent(Event event);

    public void closeConnection();

    public void addToQueue(EventImage eventImage);

}
