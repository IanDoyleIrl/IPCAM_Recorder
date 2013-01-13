package org.test.cameraMonitor.restAPI;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.EventImage;
import org.test.cameraMonitor.entities.RecordedImage;
import org.test.cameraMonitor.util.HibernateUtil;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Iterator;


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
        int totalEventCount = ((Long)HibernateUtil.getSessionFactory().openSession().createQuery("SELECT COUNT (id) FROM Event").uniqueResult()).intValue();
        int totalEventImageCount = ((Long)HibernateUtil.getSessionFactory().openSession().createQuery("SELECT COUNT (id) FROM EventImage").uniqueResult()).intValue();
        long averageImagesPerEvent = 0;
        if (totalEventCount > 0 && totalEventImageCount > 0){
            averageImagesPerEvent = totalEventCount / totalEventImageCount;
        }
        Event activeEvent = (Event)GlobalAttributes.getInstance().getAttributes().get("currentEvent");
        long currentActiveEventId = 0;
        if (activeEvent != null){
            currentActiveEventId = activeEvent.getID();
        }
        JSONObject response = new JSONObject();
        response.put("totalEventCount", totalEventCount);
        response.put("totalEventImageCount", totalEventImageCount);
        response.put("currentActiveEventId", currentActiveEventId);
        response.put("averageImagesPerEvent", averageImagesPerEvent);
        return Response.ok(response.toJSONString()).build();
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
        response.put("id", event.getID());
        response.put("timeStarted", event.getTimeStarted());
        if (event.getTimeEnded() == 0){
            response.put("timeEnded", System.currentTimeMillis());
        }
        response.put("comments", event.getComments());
        response.put("name", event.getName());
        JSONArray eventImages = new JSONArray();
        Iterator<EventImage> iter = event.getEventImages().iterator();
        while (iter.hasNext()){
            EventImage eventImage = iter.next();
            JSONObject image = new JSONObject();
            image.put("id", eventImage.getId());
            image.put("time", eventImage.getDate());
            eventImages.add(image);
        }
        Event e = (Event)GlobalAttributes.getInstance().getAttributes().get("currentEvent");
        if (e != null & e.getID() == event.getID()){
            response.put("active", true);
        }
        else{
            response.put("active", false);
        }

        response.put("eventImages", eventImages);
        }
        return Response.ok(response.toJSONString()).build();
    }




}