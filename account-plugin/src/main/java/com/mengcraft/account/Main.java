package com.mengcraft.account;

import com.mengcraft.account.bungee.BungeeMessage;
import com.mengcraft.account.bungee.BungeeSupport;
import com.mengcraft.account.command.BindingCommand;
import com.mengcraft.account.entity.AppAccountBinding;
import com.mengcraft.account.entity.AppAccountEvent;
import com.mengcraft.account.entity.Member;
import com.mengcraft.account.util.Messenger;
import com.mengcraft.account.util.MetricsLite;
import com.mengcraft.simpleorm.EbeanHandler;
import com.mengcraft.simpleorm.EbeanManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    private boolean log;
    private boolean notifyMail;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        EbeanHandler db = EbeanManager.DEFAULT.getHandler(this);
        if (!db.isInitialized()) {
            db.define(AppAccountBinding.class);
            db.define(AppAccountEvent.class);
            db.define(Member.class);
            try {
                db.initialize();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        db.install(true);
        db.reflect();

        Account.INSTANCE.setMain(this);
        log = getConfig().getBoolean("log");
        notifyMail = getConfig().getBoolean("notify.mail");

        if (!getConfig().getBoolean("minimal")) {
            Executor exec = new Executor(this, new Messenger(this));
            exec.bind();
            exec.addExecLogin();
            exec.addExecRegister();

            new EventListener().bind(this);

            if (getConfig().getBoolean("binding.command")) {
                getCommand("binding").setExecutor(new BindingCommand(this));
            }

            getServer().getMessenger().registerIncomingPluginChannel(this, BungeeMessage.CHANNEL, BungeeSupport.INSTANCE);
            getServer().getMessenger().registerOutgoingPluginChannel(this, BungeeMessage.CHANNEL);
        }

        new MetricsLite(this).start();

        String[] j = {
                ChatColor.GREEN + "梦梦家高性能服务器出租店",
                ChatColor.GREEN + "shop105595113.taobao.com"
        };
        getServer().getConsoleSender().sendMessage(j);
    }

    public void execute(Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

    public void run(Runnable task, int tick) {
        getServer().getScheduler().runTaskLater(this, task, tick);
    }

    public void run(Runnable task) {
        getServer().getScheduler().runTask(this, task);
    }

    public boolean notifyMail() {
        return notifyMail;
    }

    public boolean isLog() {
        return log;
    }

    public static boolean nil(Object object) {
        return object == null;
    }

}
