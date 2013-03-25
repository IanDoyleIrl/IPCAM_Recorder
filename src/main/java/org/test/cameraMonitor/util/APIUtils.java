package org.test.cameraMonitor.util;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.json.simple.JSONObject;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.Image;
import org.test.cameraMonitor.entities.RecordedImage;

import javax.persistence.Table;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 19/01/2013
 * Time: 14:19
 * To change this template use File | Settings | File Templates.
 */
public abstract class APIUtils {

    private static String queryString = "FROM RecordedImage WHERE Date > :start AND Date < :end ORDER BY Date ASC";

    public static ByteArrayOutputStream getZipFromImageArray(ArrayList<Image> images, String fileName) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zipStream = new ZipOutputStream(baos);
        try{
            for (Image i : images){
                i = ImageUtils.putTimpStampOnImage(i, APIUtils.getTimestampFromLong(i.getDate()));
                byte[] buf = new byte[1024];
                ZipEntry entry = new ZipEntry(String.valueOf(i.getDate()) + ".jpeg");
                zipStream.putNextEntry(entry);
                int len;
                ByteArrayInputStream inputStream = new ByteArrayInputStream(i.getImageData());
                while ((len = inputStream.read(buf)) > 0) {
                    zipStream.write(buf, 0, len);
                }
                zipStream.closeEntry();
            }
            zipStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return baos;
    }

    public static String getTimestampFromLong(long date) {
        Date dt = new Date(date);
        SimpleDateFormat ft = new SimpleDateFormat ("dd/MM/yyyy - hh:mm:ss");
        return ft.format(dt);
    }

    public static List<Image> getRecordingArrayByStartEndDate(long start, long end){
        org.hibernate.Query query = HibernateUtil.getSessionFactory().openSession().createQuery(APIUtils.queryString);
        query.setParameter("start", start);
        query.setParameter("end", end);
        List<RecordedImage> list = query.list();
        return query.list();
    }

    public static String getUpdateJSON() {
        JSONObject response = new JSONObject();
        response.put("latestEvent", APIUtils.getLatestEvent());
        if (GlobalAttributes.getInstance().getCurrentEvent() == null){
            response.put("activeRecording", false);
        }
        else{
            response.put("activeRecording", true);
        }
        response.put("recordingStats", APIUtils.getRecordingStats());
        response.put("eventStats", APIUtils.getEventStats());
        return response.toJSONString();
    }

    public static JSONObject getEventStats() {
        int totalEventCount = ((Long) HibernateUtil.getSessionFactory().openSession().createQuery("SELECT COUNT (id) FROM Event").uniqueResult()).intValue();
        int totalEventImageCount = ((Long)HibernateUtil.getSessionFactory().openSession().createQuery("SELECT COUNT (id) FROM EventImage").uniqueResult()).intValue();
        long averageImagesPerEvent = 0;
        if (totalEventCount > 0 && totalEventImageCount > 0){
            averageImagesPerEvent = totalEventImageCount / totalEventCount;
        }
        Event activeEvent = GlobalAttributes.getInstance().getCurrentEvent();
        long currentActiveEventId = 0;
        if (activeEvent != null){
            currentActiveEventId = activeEvent.getID();
        }
        JSONObject response = new JSONObject();
        response.put("totalEventCount", totalEventCount);
        response.put("totalEventImageCount", totalEventImageCount);
        response.put("currentActiveEventId", currentActiveEventId);
        response.put("isEventActive", (Boolean)GlobalAttributes.getInstance().isEventTriggered());
        response.put("averageImagesPerEvent", averageImagesPerEvent);
        response.put("lastEventTimestamp", GlobalAttributes.getInstance().getEventTimestamp());
        return response;
    }

    public static JSONObject getRecordingStats(){
        int totalImageCount = ((Long)HibernateUtil.getSessionFactory().openSession().createQuery("SELECT COUNT (id) FROM RecordedImage").uniqueResult()).intValue();

        DetachedCriteria detachedCriteria = null;
        Criteria query = null;
        RecordedImage image = null;

        detachedCriteria = DetachedCriteria.forClass( RecordedImage.class );
        detachedCriteria.setProjection( Projections.max( "Id" ) );
        query = HibernateUtil.getSessionFactory().openSession().createCriteria( RecordedImage.class );
        query.add( Property.forName("Id").eq( detachedCriteria ) );
        image = (RecordedImage) query.uniqueResult();
        int maxId = image.getId();

        detachedCriteria = DetachedCriteria.forClass( RecordedImage.class );
        detachedCriteria.setProjection( Projections.min( "Id" ) );
        query = HibernateUtil.getSessionFactory().openSession().createCriteria( RecordedImage.class );
        query.add( Property.forName("Id").eq( detachedCriteria ) );
        image = (RecordedImage) query.uniqueResult();
        long startTime = image.getDate();

        long totalTimeInSeconds = (System.currentTimeMillis() - startTime)/ 1000;
        long averageFPS = totalImageCount / totalTimeInSeconds;


        JSONObject response = new JSONObject();
        response.put("totalImageCount", totalImageCount);
        response.put("maxId", maxId);
        response.put("startTime", startTime);
        response.put("FPS", GlobalAttributes.getInstance().getConfigValue("FramesPerSecond"));
        response.put("tableSize", DatabaseUtils.getTableSizeByName(RecordedImage.class.getAnnotation(Table.class).name()));
        return response;
    }

    public static JSONObject getLatestEvent(){
        DetachedCriteria detachedCriteria = null;
        Criteria query = null;
        Event event = null;

        detachedCriteria = DetachedCriteria.forClass( Event.class );
        detachedCriteria.setProjection( Projections.max( "ID" ) );
        query = HibernateUtil.getSessionFactory().openSession().createCriteria(Event.class);
        query.add( Property.forName("ID").eq( detachedCriteria ) );
        event = (Event) query.uniqueResult();

        JSONObject response = new JSONObject();
        if (event != null){
            response = EventUtils.createEventJSON(event);
            return response;
        }
        else{
            return null;
        }
    }
}
