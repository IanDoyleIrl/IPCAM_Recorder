package org.test.cameraMonitor.restAPI;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: dreambrotherirl
 * Date: 27/01/2013
 * Time: 12:46
 * To change this template use File | Settings | File Templates.
 */
@Path("/Settings")
public class SettingsAPI {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/dropdownOptions/{name}/{type}")
    public Response getEventsByHours(@PathParam("name") String name, @PathParam("type") String type) throws IOException {
        if (name.equalsIgnoreCase("event")){
            if (type.equalsIgnoreCase("hours")){
                JSONObject response = new JSONObject();
                JSONArray array = new JSONArray();
                array.add(this.getOptionInJSON("1", "1"));
                array.add(this.getOptionInJSON("2", "2"));
                array.add(this.getOptionInJSON("4", "4"));
                array.add(this.getOptionInJSON("8", "8"));
                array.add(this.getOptionInJSON("12", "12"));
                response.put("values", array);
                return Response.ok(response.toJSONString()).build();
            }
            else if (type.equalsIgnoreCase("type")){
                JSONObject response = new JSONObject();
                JSONArray array = new JSONArray();
                array.add(this.getOptionInJSON("Minutes", "minutes"));
                array.add(this.getOptionInJSON("Hours", "hours"));
                array.add(this.getOptionInJSON("Days", "days"));
                array.add(this.getOptionInJSON("Weeks", "weeks"));
                array.add(this.getOptionInJSON("Months", "months"));
                response.put("values", array);
                return Response.ok(response.toJSONString()).build();
            }
        }
        return Response.status(404).build();
    }

    public static JSONObject getOptionInJSON(String name, String id){
        JSONObject result = new JSONObject();
        result.put("name", name);
        result.put("id", id);
        return  result;
    }

}
