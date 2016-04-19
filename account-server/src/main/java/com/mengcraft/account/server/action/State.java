package com.mengcraft.account.server.action;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created on 16-4-19.
 */
@Path("/state/{name}")
@Produces(MediaType.APPLICATION_JSON)
public class State {

    @GET
    public StateResponse process(@PathParam("name") String name) {
        return new StateResponse();
    }

}
