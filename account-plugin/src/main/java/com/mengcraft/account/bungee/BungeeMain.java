package com.mengcraft.account.bungee;

import com.mengcraft.account.util.ReadWriteUtil;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.DataInput;

import static com.mengcraft.account.util.Util.eq;

/**
 * Created on 16-2-17.
 */
public class BungeeMain extends Plugin implements Listener {

    public static final String CHANNEL = "AccountBungee";
    public static final byte DISTRIBUTE = 0;
    public static final byte ADD_LOGGED = 1;
    public static final byte DEL_LOGGED = 2;

    @Override
    public void onEnable() {
        getProxy().registerChannel(CHANNEL);
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void handle(PluginMessageEvent event) {
        if (eq(event.getTag(), CHANNEL)) {
            processMessage(event.getSender(), event.getData());
        }
    }

    private void processMessage(Connection sender, byte[] data) {
        if (sender instanceof Server) {
            DataInput input = ReadWriteUtil.toDataInput(data);
            BungeeMessage message = BungeeMessage.read(input);
            if (message.valid() && eq(message.getType(), DISTRIBUTE)) {
                message.setType(ADD_LOGGED);
                message.broadcast(getProxy().getServers().values());
            }
        }
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        BungeeMessage message = new BungeeMessage();
        message.setType(DEL_LOGGED);
        message.setName(event.getPlayer().getName());
        message.broadcast(getProxy().getServers().values());
    }

}
