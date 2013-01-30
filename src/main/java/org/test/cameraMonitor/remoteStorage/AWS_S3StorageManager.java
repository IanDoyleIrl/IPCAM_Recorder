package org.test.cameraMonitor.remoteStorage;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.EventImage;
import org.test.cameraMonitor.util.HibernateUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 26/01/2013
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
public class AWS_S3StorageManager implements RemoteStorageManager {

    AWSCredentials credentials;
    AmazonS3 conn;
    String accessKey;
    String secretKey;
    String regionID;
    Bucket root;

    public AWS_S3StorageManager(){
        this.createConnection();
    }



    private void createConnection(){
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        conn = new AmazonS3Client(credentials);
        this.root = this.getRootBucket();
    }

    private Bucket getRootBucket(){
        for (Bucket b : conn.listBuckets()){
            if (b.getName().equals("securitysystem")){
                return b;
            }
        }
        return null;
    }

    private Bucket getEventFolder(Event event){
        List<Bucket> buckets = conn.listBuckets();
        for (Bucket bucket : buckets) {
            if (bucket.getName().equals(event.getName() + "-" + event.getTimeStarted())){;
                return bucket;
            }
        }
        return null;
    }

    private String getBucketNameFromEvent(Event event){
        return "securitysysytem/" + event.getName() + "-" + event.getID();

    }

    private String getFileNameFromEventImage(EventImage image){
        return String.valueOf(image.getDate()) + ".jpeg";
    }

    private String getUploadNameFromEventImage(EventImage img){
        return getBucketNameFromEvent(img.getEvent()) + "/" + this.getFileNameFromEventImage(img);
    }

    @Override
    public void uploadImageFromEvent(EventImage image) {
        ByteArrayInputStream input = new ByteArrayInputStream(image.getImageData());
        ObjectMetadata metadata = new ObjectMetadata();
        Map<String, String> metaData = new HashMap<String, String>();
        metaData.put("name", String.valueOf(image.getDate()));
        metaData.put("eventId", String.valueOf(image.getEvent().getID()));
        metadata.setUserMetadata(metaData);
        conn.putObject(root.getName(), this.getUploadNameFromEventImage(image) + ".jpeg", input, metadata);
        System.out.println("Uploaded: " + root.getName() + "/" + this.getUploadNameFromEventImage(image) + ".jpeg");
    }

    @Override
    public boolean isEventImageSynced(EventImage image) {
        Bucket bucket = this.getEventFolder(image.getEvent());
        S3Object response = conn.getObject(new GetObjectRequest(bucket.getName(), String.valueOf(image.getDate())));
        if (response == null){
            return false;
        }
        return true;
    }

    @Override
    public EventImage getRemoteCopyOfEventImage(EventImage image) throws IOException{
        S3Object response = conn.getObject(new GetObjectRequest(root.getName(), this.getUploadNameFromEventImage(image)));
        Event event = (Event)HibernateUtil.getSessionFactory().openSession().get(Event.class, response.getObjectMetadata().getUserMetadata().get("eventId"));
        EventImage newImage = new EventImage();
        newImage.setEvent(event);
        newImage.setDate(Long.valueOf(response.getObjectMetadata().getUserMetadata().get("name")));
        newImage.setImageData(org.apache.commons.io.IOUtils.toByteArray(response.getObjectContent()));
        return newImage;
    }

    @Override
    public ArrayList<EventImage> getAllImagesByEvent(Event event) {
        Bucket bucket = this.getEventFolder(event);
        ArrayList<EventImage> eventImages = new ArrayList<EventImage>();
        ObjectListing images = conn.listObjects(bucket.getName());
        for (S3ObjectSummary summary : images.getObjectSummaries()){
            S3Object response = conn.getObject(bucket.getName(), summary.getKey());
            eventImages.add(this.getEventImageFromS3Response(response));
        }
        return eventImages;
    }

    @Override
    public void closeConnection() {
    }

    @Override
    public void addToQueue(EventImage eventImage) {
        try {
            queue.put(eventImage);
        } catch (InterruptedException e) {

        }
    }

    private EventImage getEventImageFromS3Response(S3Object response){
        try{
            Event event = (Event)HibernateUtil.getSessionFactory().openSession().get(Event.class, response.getObjectMetadata().getUserMetadata().get("eventId"));
            EventImage newImage = new EventImage();
            newImage.setEvent(event);
            newImage.setDate(Long.valueOf(response.getObjectMetadata().getUserMetadata().get("name")));
            newImage.setImageData(org.apache.commons.io.IOUtils.toByteArray(response.getObjectContent()));
            return newImage;
        }
        catch (IOException e){
            return null;
        }
    }

    @Override
    public void run() {
        while (true){
            try{
                EventImage eventImage = queue.take();
                this.uploadImageFromEvent(eventImage);
            }
            catch (Exception e){

            }
        }
    }
}
