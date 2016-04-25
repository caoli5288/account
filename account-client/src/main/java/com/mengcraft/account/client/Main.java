package com.mengcraft.account.client;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created on 16-4-25.
 */
public class Main extends JavaPlugin {

    public static final String CHANNEL = "Account";

    public void execute(Runnable runnable, boolean sync) {
        if (sync) {
            getServer().getScheduler().runTask(this, runnable);
        } else {
            getServer().getScheduler().runTaskAsynchronously(this, runnable);
        }
    }

}
