package com.mengcraft.account.client;

import com.mengcraft.account.bungee.BungeeMessage;
import com.mengcraft.account.util.Messenger;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created on 16-4-25.
 */
public class Main extends JavaPlugin {

    public static final String CHANNEL = "Account";

    @Override
    public void onEnable() {
        saveDefaultConfig();

        ChannelHandler handler = new ChannelHandler();
        handler.setServer(getConfig().getString("server"));
        handler.setMain(this);
        handler.setMessenger(new Messenger(this));

        getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, handler);
        getServer().getMessenger().registerOutgoingPluginChannel(this, BungeeMessage.CHANNEL);
    }

    public void execute(Runnable runnable, boolean sync) {
        if (sync) {
            getServer().getScheduler().runTask(this, runnable);
        } else {
            getServer().getScheduler().runTaskAsynchronously(this, runnable);
        }
    }

}
