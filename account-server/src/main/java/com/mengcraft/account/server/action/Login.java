package com.mengcraft.account.server.action;

import com.mengcraft.account.server.Main;
import com.mengcraft.account.server.entity.BeanUser;
import com.mengcraft.account.server.lib.MD5Util;
import com.mengcraft.account.server.lib.SessionBuilder;
import com.mengcraft.account.server.lib.SessionMap;
import com.mengcraft.account.server.lib.TimeoutMap;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import static com.avaje.ebean.Ebean.find;

/**
 * Created by on 16-4-14.
 */
@Path("/login/{name}")
public class Login {

    private static final TimeoutMap QUERY_TIMEOUT = new TimeoutMap(1500);

    static final TimeoutMap SESSION_TIMEOUT = new TimeoutMap(86400000);
    static final SessionMap SESSION_MAP = new SessionMap();

    @GET
    public void process(@BeanParam LoginRequest request, @Suspended AsyncResponse context) {
        LoginResponse response = new LoginResponse();
        if (QUERY_TIMEOUT.isOnTime(request.getIPAddress())) {
            response.setError("Server busy!");
        } else {
            QUERY_TIMEOUT.put(request.getIPAddress());
            Main.POOL.execute(() -> {
                execute(request, response);
            });
        }
        context.resume(response);
    }

    private void execute(LoginRequest request, LoginResponse response) {
        BeanUser user = find(BeanUser.class)
                .where()
                .eq("username", request.getName())
                .findUnique();
        if (user == null) {
            response.setError("Password error!");
        } else {
            MD5Util util = new MD5Util();
            try {
                String digest = util.digest(request.getSecure() + user.getSalt());
                if (user.getPassword().equals(digest)) {
                    accept(request, response);
                } else {
                    response.setError("Password error!");
                }
            } catch (Exception ignored) {
                response.setError("Server busy!");
            }
        }
    }

    private void accept(LoginRequest request, LoginResponse response) {
        String session = new SessionBuilder().nextSession();
        SESSION_MAP.put(request.getName(), session);
        SESSION_TIMEOUT.put(request.getName());
        response.setSession(session);
    }

}
