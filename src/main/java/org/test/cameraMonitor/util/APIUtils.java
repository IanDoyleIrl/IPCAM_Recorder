package org.test.cameraMonitor.util;

import org.test.cameraMonitor.entities.Image;
import org.test.cameraMonitor.entities.RecordedImage;

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

}
