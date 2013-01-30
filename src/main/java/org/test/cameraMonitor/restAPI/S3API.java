package org.test.cameraMonitor.restAPI;

import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.util.EventUtils;
import org.test.cameraMonitor.util.HibernateUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 27/01/2013
 * Time: 12:46
 * To change this template use File | Settings | File Templates.
 */
@Path("/S3")
public class S3API {

    @Path("/test")
    @GET
    @Produces({"application/zip"})
    public StreamingOutput getPDF() throws Exception {
        final Event event = (Event)HibernateUtil.getSessionFactory().openSession().get(Event.class, 4);
        StreamingOutput stream = new StreamingOutput() {
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                    output = EventUtils.getZipFromEventImages(event.getEventImages());
                    output.flush();
                    output.close();
                } catch (Exception e) {
                    throw new WebApplicationException(e);
                }
            }
        };
        System.out.println(stream.toString());
        return stream;
    }





}
