package org.test.cameraMonitor.restAPI;

import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.EventImage;
import org.test.cameraMonitor.entities.Image;
import org.test.cameraMonitor.util.APIUtils;
import org.test.cameraMonitor.util.HibernateUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 27/01/2013
 * Time: 12:46
 * To change this template use File | Settings | File Templates.
 */
@Path("/S3")
public class S3API {

    @Path("/event/{id}")
    @GET
    @Produces({"application/zip"})
    public Response getZipFromS3Event(@PathParam("id") int id) throws IOException{
        Event e = (Event)HibernateUtil.getSessionFactory().openSession().get(Event.class, id);
        ArrayList<Image> images = GlobalAttributes.getInstance().getS3StorageManager().getAllImagesByEvent
                ((Event)HibernateUtil.getSessionFactory().openSession().get(Event.class, id));
        byte[] zipContents = APIUtils.getZipFromImageArray(images, e.getName()).toByteArray();
        return Response
                .ok(zipContents, "application/zip")
                .header("content-disposition","attachment; filename = " + e.getName() + ".zip")
                .build();
    }

    @Path("/test/event/{id}")
    @GET
    @Produces({"application/zip"})
    public Response getZipFromEvent(@PathParam("id") int id) throws IOException{
        Event e = (Event)HibernateUtil.getSessionFactory().openSession().get(Event.class, id);
        ArrayList<Image> images = new ArrayList(e.getEventImages());
        byte[] zipContents = APIUtils.getZipFromImageArray(images, e.getName()).toByteArray();

        return Response.ok(zipContents).build();

    }


    @GET
    @Produces("image/jpeg")
    @Path("/event/image/{id}")
    public Response getImageFromId(@PathParam("id") int id) throws IOException {
        EventImage image = (EventImage)(HibernateUtil.getSessionFactory().openSession().get(EventImage.class, id));
        return Response.ok(GlobalAttributes.getInstance().getS3StorageManager().getRemoteCopyOfEventImage(image).getImageData()).build();

    }





}
