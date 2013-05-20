package org.test.cameraMonitor.restAPI;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.Camera;
import org.test.cameraMonitor.entities.Event;
import org.test.cameraMonitor.entities.EventImage;
import org.test.cameraMonitor.entities.Image;
import org.test.cameraMonitor.streamingServer.EventStream;
import org.test.cameraMonitor.streamingServer.StreamingUtils;
import org.test.cameraMonitor.util.*;

import javax.servlet.http.HttpServlet;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


// POJO, no interface no extends

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/stream/")
public class StreamingAPI extends HttpServlet {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/eventStream/{id}")
    public Response get(@PathParam("hours") int hours) throws IOException {
        long currentDate = System.currentTimeMillis();
        long previousDate = currentDate - (3600000 * hours);
        Query query = HibernateUtil.getSessionFactory().openSession().createQuery
                ("FROM Event WHERE timeStarted >= :beginTime");
        query.setLong("beginTime", previousDate);
        List<Event> events = query.list();
        Iterator<Event> iterator = events.iterator();
        JSONObject response = new JSONObject();
        JSONArray eventsJSON = new JSONArray();
        while (iterator.hasNext()){
            JSONObject tempJSON = EventUtils.createEventJSON(iterator.next());
            eventsJSON.add(tempJSON);
        }
        response.put("events", eventsJSON);
        return Response.ok(response.toJSONString()).build();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/eventStream/")
    public Response Post(String json) throws IOException {
        final ExecutorService executor = Executors.newSingleThreadExecutor();
        JSONObject streamingJSON = (JSONObject) JSONValue.parse(json);
        long id = (Long)streamingJSON.get("eventId");
        Event event = (Event)new HibernateUtil().getSessionFactory().openSession().get(Event.class, (int)id);
        EventStream stream = new EventStream(event);
        String uuid = StreamingUtils.generateRandomStreamId();
        GlobalAttributes.getInstance().getEventStreamTable().put(uuid, stream);
        executor.submit(stream);
        JSONObject response = new JSONObject();
        response.put("eventId", event.getID());
        response.put("streamId", uuid);
        return Response.ok(response.toJSONString()).build();
    }






}