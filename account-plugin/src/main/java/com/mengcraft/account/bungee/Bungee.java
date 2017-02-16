package com.mengcraft.account.bungee;

import com.mengcraft.account.util.$;
import com.mengcraft.account.util.ReadWriteUtil;
import net.md_5.bungee.api.connection.Connection;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.DataInput;

/**
 * Created on 16-2-17.
 */
public class Bungee extends Plugin implements Listener {

    @Override
    public void onEnable() {
        getProxy().registerChannel(BungeeMessage.CHANNEL);
        getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void handle(PluginMessageEvent event) {
        if ($.eq(event.getTag(), BungeeMessage.CHANNEL)) {
            processMessage(event.getSender(), event.getData());
        }
    }

    private void processMessage(Connection sender, byte[] data) {
        if (sender instanceof Server) {
            DataInput input = ReadWriteUtil.toDataInput(data);
            BungeeMessage message = BungeeMessage.read(input);
            if (message.valid() && $.eq(message.getType(), BungeeMessage.DISTRIBUTE)) {
                message.setType(BungeeMessage.ADD);
                message.broadcast(getProxy().getServers().values());
            }
        }
    }

    @EventHandler
    public void handle(PlayerDisconnectEvent event) {
        BungeeMessage message = new BungeeMessage();
        message.setType(BungeeMessage.DEL);
        message.setName(event.getPlayer().getName());
        message.broadcast(getProxy().getServers().values());
    }

}
