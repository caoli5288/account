package com.mengcraft.account.bungee;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 16-2-17.
 */
public class Main extends Plugin implements Listener {

    private final Map<String, String> secureMap = new HashMap<>();

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().registerChannel(TAG);
    }

    @EventHandler
    public void handle(PluginMessageEvent event) {
        if (TAG.equals(event.getTag()) && event.getReceiver() instanceof ProxiedPlayer) {
            ByteArrayDataInput input = ByteStreams.newDataInput(event.getData());
            if (input.readByte() == 0) {
                secureMap.put(input.readUTF(), input.readUTF());
            } else {
                String name = input.readUTF();
                if (secureMap.containsKey(name)) {
                    ByteArrayDataOutput output = ByteStreams.newDataOutput();
                    output.writeUTF(name);
                    output.writeUTF(secureMap.get(name));
                    ((Server) event.getSender()).getInfo().sendData(TAG, output.toByteArray());
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        secureMap.remove(event.getPlayer().getName());
    }

    public static final String TAG = "AccountBungeeSession";

}
