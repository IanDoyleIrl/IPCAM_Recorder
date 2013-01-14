package org.test.cameraMonitor.restAPI;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.json.simple.JSONObject;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.EventImage;
import org.test.cameraMonitor.entities.RecordedImage;
import org.test.cameraMonitor.util.EventUtils;
import org.test.cameraMonitor.util.HibernateUtil;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;


// POJO, no interface no extends

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/event")
public class EventsAPI extends HttpServlet {

    @GET
    @Produces("image/jpeg")
    @Path("/image/{id}")
    public Response getImageFromId(@PathParam("id") int id) throws IOException {
        EventImage image = (EventImage) HibernateUtil.getSessionFactory().openSession().get(EventImage.class, id);
        if (image == null){
            throw new WebApplicationException(404);
        }
        return Response.ok(image.getImageData()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/stats")
    public Response getEventStatistics(@PathParam("id") int id) throws IOException {
        JSONObject response = EventUtils.createEventStatisticsJSON();
        return Response.ok(response.toJSONString()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getEventFromId(@PathParam("id") int id) throws IOException {
        Event event = (Event) HibernateUtil.getSessionFactory().openSession().get(Event.class, id);

        JSONObject response = new JSONObject();
        if (event != null){
            response = EventUtils.createEventJSON(event);
            return Response.ok(response.toJSONString()).build();
        }
        else{
            throw new WebApplicationException(404);
        }

    }

    @GET
    @Produces("image/jpeg")
    @Path("/image/latest")
    public Response getLatestImage() throws IOException {
        DetachedCriteria maxQuery = DetachedCriteria.forClass( RecordedImage.class );
        maxQuery.setProjection( Projections.max( "Id" ) );
        Criteria query = HibernateUtil.getSessionFactory().openSession().createCriteria( EventImage.class );
        query.add( Property.forName("Id").eq(maxQuery) );
        RecordedImage image = (RecordedImage) query.uniqueResult();
        if (image == null){
            throw new WebApplicationException(404);
        }
        return Response.ok(image.getImageData()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/latest")
    public Response getLatestEvent() throws IOException {
        DetachedCriteria detachedCriteria = null;
        Criteria query = null;
        Event event = null;

        detachedCriteria = DetachedCriteria.forClass( Event.class );
        detachedCriteria.setProjection( Projections.max( "ID" ) );
        query = HibernateUtil.getSessionFactory().openSession().createCriteria( Event.class );
        query.add( Property.forName("ID").eq( detachedCriteria ) );
        event = (Event) query.uniqueResult();

        JSONObject response = new JSONObject();
        if (event != null){
            response = EventUtils.createEventJSON(event);
            return Response.ok(response.toJSONString()).build();
        }
        else{
            throw new WebApplicationException(404);
        }
    }




}