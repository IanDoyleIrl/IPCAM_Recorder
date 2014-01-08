package org.test.cameraMonitor.restAPI;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.test.cameraMonitor.constants.GlobalAttributes;
import org.test.cameraMonitor.entities.Camera;
import org.test.cameraMonitor.entities.Image;
import org.test.cameraMonitor.entities.RecordedImage;
import org.test.cameraMonitor.util.CameraUtil;
import org.test.cameraMonitor.util.HibernateUtil;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 17/01/2013
 * Time: 23:34
 * To change this template use File | Settings | File Templates.
 */
@Path("/camera")
public class CameraAPI {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCamera(String json){
        JSONObject cameraJSON = (JSONObject)JSONValue.parse(json);
        Camera camera = new Camera();
        camera.setName((String) cameraJSON.get("name"));
        boolean activeValue = (Boolean)cameraJSON.get("active");
        camera.setActive(activeValue);
        camera.setUrl((String) cameraJSON.get("url"));
        HibernateUtil.getSessionFactory().openSession().save(camera);
        List<Camera> cameras = HibernateUtil.getSessionFactory().openSession().createQuery("From Camera").list();
        Iterator<Camera> cameraIterator = cameras.iterator();
        JSONArray response = new JSONArray();
        while (cameraIterator.hasNext()){
            response.add(CameraUtil.getCameraJSON(cameraIterator.next(), false, false));
        }
        return Response.ok(response.toJSONString()).header("Access-Control-Allow-Origin", "*").build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllCameras(){
        List<Camera> cameras = HibernateUtil.getSessionFactory().openSession().createQuery("From Camera").list();
        Iterator<Camera> cameraIterator = cameras.iterator();
        JSONArray response = new JSONArray();
        while (cameraIterator.hasNext()){
            response.add(CameraUtil.getCameraJSON(cameraIterator.next(), false, false));
        }
        return Response.ok(response.toJSONString())
                .header("Access-Control-Allow-Origin", "*")
                .header("Access-Control-Allow-Methods", "POST, GET, PUT, UPDATE, OPTIONS")
                .header("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With")
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getCameraById(@PathParam("id") int id, @QueryParam("showEvents") boolean showEvents, @QueryParam("showConnectionStatus") boolean showConnectionStatus ){
        Camera camera = (Camera)HibernateUtil.getSessionFactory().openSession().get(Camera.class, id);
        if (camera == null){
            return Response.status(404).build();
        }
        return Response.ok(CameraUtil.getCameraJSON(camera, showEvents, showConnectionStatus).toJSONString()).build();
    }

    @GET
    @Produces("image/jpeg")
    @Path("/{id}/latestImage")
    public Response getLatestImage(@PathParam("id") int id){
        Camera camera = (Camera)HibernateUtil.getSessionFactory().openSession().get(Camera.class, id);
        HashMap<Integer, RecordedImage> cameraRecordedImageHashMap= GlobalAttributes.getInstance().getLatestImages();
        Image image = cameraRecordedImageHashMap.get(camera.getID());
        if (image == null){
            throw new WebApplicationException(404);
        }
        return Response.ok(image.getImageData()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/control/{controlOption}")
    public Response getCameraControlById(@PathParam("id") int id, @PathParam("controlOption") String controlOption){
        Camera camera = (Camera)HibernateUtil.getSessionFactory().openSession().get(Camera.class, id);
        boolean result = camera.handleMovement(controlOption);
        if (!result){
            return Response.status(404).build();
        }
        return Response.status(200).build();
    }

}
