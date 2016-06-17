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

    private boolean log;
    private boolean minimal;

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

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


        minimal = getConfig().getBoolean("minimal");
        Messenger messenger = new Messenger(this);
        new ExecutorCore(this, messenger).bind();
        if (!minimal) {
            log = getConfig().getBoolean("log");
            new Executor(this, messenger).bind();
            new ExecutorEvent().bind(this);
            getServer().getMessenger().registerIncomingPluginChannel(this, BungeeSupport.CHANNEL, BungeeSupport.INSTANCE);
            getServer().getMessenger().registerOutgoingPluginChannel(this, BungeeSupport.CHANNEL);
        }

        new MetricsLite(this).start();

        getServer().getConsoleSender().sendMessage(new String[]{
                ChatColor.GREEN + "梦梦家高性能服务器出租店",
                ChatColor.GREEN + "shop105595113.taobao.com"
        });
    }

    public boolean isLog() {
        return log;
    }

    public void execute(Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

    public static boolean eq(Object i, Object j) {
        return i == j || (i != null && i.equals(j));
    }

    public boolean isMinimal() {
        return minimal;
    }

    public void process(Runnable task, int tick) {
        getServer().getScheduler().runTaskLater(this, task, tick);
    }

    public void process(Runnable task) {
        getServer().getScheduler().runTask(this, task);
    }

}
