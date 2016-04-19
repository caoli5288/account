package com.mengcraft.account.server.action;

import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

/**
 * Created on 16-4-15.
 */
public class LoginRequest {

    @PathParam("name")
    public String name;

    @PathParam("secure")
    public String secure;

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

}
