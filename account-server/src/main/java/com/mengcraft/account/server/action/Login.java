package com.mengcraft.account.server.action;

import com.mengcraft.account.server.MD5Util;
import com.mengcraft.account.server.ServerMain;
import com.mengcraft.account.server.SessionBuilder;
import com.mengcraft.account.server.SessionMap;
import com.mengcraft.account.server.entity.BeanUser;

import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

import java.util.regex.Pattern;

import static com.avaje.ebean.Ebean.find;

/**
 * Created by on 16-4-14.
 */
@Path("/login/{name}/{secure}")
public class Login {

    public static final SessionMap SESSION_MAP = new SessionMap();
    public static final Pattern PATTERN = Pattern.compile("[\\w]+");

    @GET
    public void process(@BeanParam LoginRequest request, @Suspended AsyncResponse response) {
        if (!PATTERN.matcher(request.getName()).matches()) {
            response.cancel();
        } else ServerMain.POOL.execute(() -> {
            BeanUser user = find(BeanUser.class)
                    .where()
                    .eq("username", request.getName())
                    .findUnique();
            if (user == null) {
                response.cancel();
            } else {
                MD5Util util = new MD5Util();
                try {
                    String digest = util.digest(request.getSecure() + user.getSalt());
                    if (user.getPassword().equals(digest)) {
                        accept(request, response);
                    } else {
                        response.cancel();
                    }
                } catch (Exception ignored) {
                    response.cancel();
                }
            }
        });
    }

    private void accept(LoginRequest request, AsyncResponse response) {
        String session = new SessionBuilder().nextSession();
        SESSION_MAP.put(request.getName(), session);
        response.resume(session);
    }

}
