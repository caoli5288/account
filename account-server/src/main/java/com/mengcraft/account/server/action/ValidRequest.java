package com.mengcraft.account.server.action;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

/**
 * Created on 16-4-20.
 */
public class ValidRequest {

    @PathParam("name")
    public String name;

    @QueryParam("session")
    public String session;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

}
