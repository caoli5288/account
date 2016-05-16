package com.mengcraft.account.server.action;

import org.glassfish.grizzly.http.server.Request;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

/**
 * Created on 16-4-15.
 */
public class LoginRequest {

    @PathParam("name")
    public String name;

    @QueryParam("secure")
    public String secure;

    @Context
    public Request request;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSecure() {
        return secure;
    }

    public void setSecure(String secure) {
        this.secure = secure;
    }

    public String getIPAddress() {
        return request.getRemoteAddr();
    }

}
