package com.mengcraft.account;

import com.mengcraft.account.entity.AppAccountBinding;
import com.mengcraft.account.entity.AppAccountEvent;
import com.mengcraft.account.entity.User;
import com.mengcraft.account.lib.Messenger;
import com.mengcraft.account.lib.MetricsLite;
import com.mengcraft.simpleorm.EbeanHandler;
import com.mengcraft.simpleorm.EbeanManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private boolean logEvent;
    private boolean coreMode;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        setCoreMode(getConfig().getBoolean("coreMode"));
        setLogEvent(getConfig().getBoolean("logEvent"));

        EbeanHandler source = EbeanManager.DEFAULT.getHandler(this);
        if (!source.isInitialized()) {
            source.define(AppAccountBinding.class);
            source.define(AppAccountEvent.class);
            source.define(User.class);
            try {
                source.initialize();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        source.install(true);
        source.reflect();

        new ExecutorCore(this).bind();
        if (!coreMode) {
            new BungeeSession(this).bind();
            new Executor(this).bind(this);
        }

        new MetricsLite(this).start();

        String[] message = {
                ChatColor.GREEN + "梦梦家高性能服务器出租店",
                ChatColor.GREEN + "shop105595113.taobao.com"
        };
        getServer().getConsoleSender().sendMessage(message);
    }

    public void setCoreMode(boolean coreMode) {
        this.coreMode = coreMode;
    }

    public boolean isCoreMode() {
        return coreMode;
    }

    public void setLogEvent(boolean logEvent) {
        this.logEvent = logEvent;
    }

    public boolean isLogEvent() {
        return logEvent;
    }

    public void execute(Runnable runnable, boolean b) {
        if (b) {
            getServer().getScheduler().runTaskAsynchronously(this, runnable);
        } else {
            getServer().getScheduler().runTask(this, runnable);
        }
    }

}
