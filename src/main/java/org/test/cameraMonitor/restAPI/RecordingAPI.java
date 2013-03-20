package org.test.cameraMonitor.restAPI;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.json.simple.JSONObject;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.Image;
import org.test.cameraMonitor.entities.RecordedImage;
import org.test.cameraMonitor.util.APIUtils;
import org.test.cameraMonitor.util.DatabaseUtils;
import org.test.cameraMonitor.util.HibernateUtil;
import org.test.cameraMonitor.util.ImageUtils;

import javax.persistence.Table;
import javax.servlet.http.HttpServlet;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;


// POJO, no interface no extends

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/recording")
public class RecordingAPI extends HttpServlet {

    @GET
    @Produces("application/zip")
    @Path("/zip/{startTime}/{endTime}")
    public Response getImagesAsZip(@PathParam("startTime") long start, @PathParam("endTime") long end) throws IOException {
        ArrayList<Image> images = (ArrayList)APIUtils.getRecordingArrayByStartEndDate(start, end);
        byte[] zipContents = APIUtils.getZipFromImageArray(images, start + "-" + end).toByteArray();
        return Response.ok(zipContents).build();
    }

    @GET
    @Produces("image/jpeg")
    @Path("/image/{id}")
    public Response getImageFromId(@PathParam("id") int id) throws IOException {
        Image image = (RecordedImage) HibernateUtil.getSessionFactory().openSession().get(RecordedImage.class, id);
        image = ImageUtils.putTimpStampOnImage(image, APIUtils.getTimestampFromLong(image.getDate()));
        if (image == null){
            throw new WebApplicationException(404);
        }
        return Response.ok(image.getImageData()).build();
    }

    @GET
    @Produces("image/jpeg")
    @Path("/latest")
    public Response getLatestImage() throws IOException {
        DetachedCriteria maxQuery = DetachedCriteria.forClass( RecordedImage.class );
        maxQuery.setProjection( Projections.max( "Id" ) );
        Criteria query = HibernateUtil.getSessionFactory().openSession().createCriteria( RecordedImage.class );
        query.add( Property.forName("Id").eq(maxQuery) );
        Image image = (Image) query.uniqueResult();
        if (image == null){
            throw new WebApplicationException(404);
        }
        return Response.ok(ImageUtils.putTimpStampOnImage(image, APIUtils.getTimestampFromLong(image.getDate())).getImageData()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/stats")
    public Response getStats() throws IOException {
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
        return Response.ok(response.toJSONString()).build();

    }


}