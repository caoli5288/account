package com.mengcraft.account.server.action;

import com.mengcraft.account.server.lib.TimeoutMap;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * Created by on 16-4-14.
 */
@Path("/valid/{name}")
public class Valid {

    private static final TimeoutMap QUERY_TIMEOUT = new TimeoutMap(3000);

    @GET
    public ValidResponse process(@BeanParam ValidRequest request) {
        ValidResponse response = new ValidResponse();
        if (QUERY_TIMEOUT.isOnTime(request.getName())) {
            response.setValid(2);
        } else if (valid(request)) {
            response.setValid(1);
        } else {
            QUERY_TIMEOUT.put(request.getName());
        }
        return response;
    }

    private boolean valid(ValidRequest request) {
        return (Login.SESSION_TIMEOUT.isOnTime(request.getName()) &&
                Login.SESSION_MAP.containsKey(request.getName()) &&
                Login.SESSION_MAP.get(request.getName()).equals(request.getSession())
        );
    }

}
