package org.test.cameraMonitor.restAPI;

import org.test.cameraMonitor.entities.Camera;
import org.test.cameraMonitor.util.CameraUtil;
import org.test.cameraMonitor.util.HibernateUtil;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 17/01/2013
 * Time: 23:34
 * To change this template use File | Settings | File Templates.
 */
@Path("/camera")
public class CameraAPI {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public Response getCameraById(@PathParam("id") int id){
        Camera camera = (Camera)HibernateUtil.getSessionFactory().openSession().get(Camera.class, id);
        if (camera == null){
            return Response.status(404).build();
        }
        return Response.ok(CameraUtil.getCameraJSON(camera).toJSONString()).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}/control/{controlOption}")
    public Response getCameraById(@PathParam("id") int id, @PathParam("controlOption") String controlOption){
        Camera camera = (Camera)HibernateUtil.getSessionFactory().openSession().get(Camera.class, id);
        boolean result = camera.handleMovement(controlOption);
        if (!result){
            return Response.status(404).build();
        }
        return Response.status(200).build();
    }

}
