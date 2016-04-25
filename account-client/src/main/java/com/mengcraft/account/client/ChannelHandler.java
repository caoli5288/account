package com.mengcraft.account.client;

import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created on 16-4-25.
 */
public class ChannelHandler {

    public final Main main;
    public String server;

    public ChannelHandler(Main main) {
        this.main = main;
    }

    public void onPluginMessageReceived(String tag, Player p, byte[] data) {
        if (Main.CHANNEL.equals(tag)) {
            // TODO
        }
    }

    private boolean valid(String name, String session) {
        try {
            URL url = new URL(server + "/valid" + '/' + name + '/' + session);
            URLConnection connection = url.openConnection();
            InputStreamReader input = new InputStreamReader(connection.getInputStream());

            JSONObject obj = (JSONObject) JSONValue.parseWithException(input);

            return obj.get("valid").equals(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setServer(String server) {
        this.server = server;
    }

}
