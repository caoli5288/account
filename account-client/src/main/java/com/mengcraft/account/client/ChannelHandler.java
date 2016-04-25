package com.mengcraft.account.client;

import com.mengcraft.account.LockedList;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created on 16-4-25.
 */
public class ChannelHandler implements PluginMessageListener {

    private final LockedList locked = LockedList.INSTANCE;
    private final Main main;
    private String server;

    public ChannelHandler(Main main) {
        this.main = main;
    }

    @Override
    public void onPluginMessageReceived(String tag, Player p, byte[] data) {
        if (Main.CHANNEL.equals(tag)) {
            execute(p, ReadWriteUtil.toDataInput(data));
        }
    }

    private void execute(Player p, DataInput input) {
        try {
            valid(p, input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void valid(Player p, DataInput input) throws IOException {
        String name = input.readUTF();
        if (p.getName().equals(name) && locked.isLocked(p.getUniqueId())) {
            String session = input.readUTF();
            main.execute(() -> {
                if (valid(name, session)) {
                    main.execute(() -> {
                        locked.remove(p.getUniqueId());
                    }, true);
                }
            }, false);
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
