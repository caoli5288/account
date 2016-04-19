package com.mengcraft.account.server.action;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;

/**
 * Created by on 16-4-14.
 */
@Path("/valid/{name}/{session}")
public class Valid {

    @GET
    public ValidResponse process(@PathParam("name") String name, @PathParam("session") String session) {
        ValidResponse response = new ValidResponse();
        if (Login.SESSION_MAP.containsKey(name) && Login.SESSION_MAP.get(name).equals(session)) {
            response.setValid(1);
        }
        return response;
    }

}
