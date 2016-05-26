package com.mengcraft.account.bungee;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created on 16-2-17.
 */
public class Main extends Plugin implements Listener {

    public static final String CHANNEL = "AccountBungeeSession";

    @Override
    public void onEnable() {
        getProxy().registerChannel(CHANNEL);
    }

    @EventHandler
    public void handle(PluginMessageEvent event) {
        if (eq(event.getTag(), CHANNEL) && event.getSender() instanceof Server) {
            DataInput input = ReadWriteUtil.toDataInput(event.getData());
            byte b;
            String name;
            String ip;
            try {
                b = input.readByte();
                name = input.readUTF();
                ip = input.readUTF();
            } catch (IOException ignored) {
                b = -1;
                name = null;
                ip = null;
            }
            event.setCancelled(true);
            if (b == 0) {
                distribute(name, ip);
            }
        }
    }

    private void distribute(String name, String ip) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutput output = ReadWriteUtil.toDataOutput(buf);
        try {
            output.writeByte(1);
            output.writeUTF(name);
            output.writeUTF(ip);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        distribute(buf.toByteArray());
    }

    private void distribute(byte[] data) {
        for (ServerInfo info : getProxy().getServers().values()) {
            info.sendData(CHANNEL, data, true);
        }
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutput output = ReadWriteUtil.toDataOutput(buf);
        try {
            output.writeByte(2);
            output.writeUTF(event.getPlayer().getName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        distribute(buf.toByteArray());
    }

    private boolean eq(Object i, Object j) {
        return i == j || (i != null && i.equals(j));
    }

}
