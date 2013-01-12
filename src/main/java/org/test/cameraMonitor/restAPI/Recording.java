package org.test.cameraMonitor.restAPI;

import org.test.cameraMonitor.entities.RecordedImage;
import org.test.cameraMonitor.recordingEngine.ConnectionUtil;
import org.test.cameraMonitor.util.HibernateUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;


// POJO, no interface no extends

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/recording")
public class Recording {

    @GET
    @Produces("image/jpeg")
    @Path("/image/{id}")
    public Response getImageFromId(@PathParam("id") int id) throws IOException {
        RecordedImage image = (RecordedImage) HibernateUtil.getSessionFactory().openSession().get(RecordedImage.class, id);
        return Response.ok(image.getImageData()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getImage() {
        return "test";
    }

    @GET
    @Produces("video/x-motion-jpeg")
    @Path("/live")
    public Response getLiveStream() throws IOException {
        boolean done = true;
        while (true){
            byte[] data = ConnectionUtil.getLastImage();
            return Response.ok(data).header("Content-Length", "-1").build();
        }
    }




}