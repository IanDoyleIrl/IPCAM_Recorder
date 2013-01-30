package org.test.cameraMonitor.test;

import org.test.cameraMonitor.entities.EventImage;
import org.test.cameraMonitor.remoteStorage.AWS_S3StorageManager;
import org.test.cameraMonitor.util.HibernateUtil;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 26/01/2013
 * Time: 18:54
 * To change this template use File | Settings | File Templates.
 */
public class S3Tester {

    public static void main(String[] args){
        AWS_S3StorageManager s3 = new AWS_S3StorageManager();
        List<EventImage> imageList = HibernateUtil.getSessionFactory().openSession().createQuery("From EventImage Where Id < 100").list();
        for (EventImage img : imageList) {
            s3.uploadImageFromEvent(img);
        }
    }


}
