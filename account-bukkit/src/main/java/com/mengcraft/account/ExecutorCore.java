package com.mengcraft.account;

import com.avaje.ebean.EbeanServer;
import com.mengcraft.account.entity.User;
import com.mengcraft.account.event.UserFetchedEvent;
import com.mengcraft.account.lib.Messenger;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

public class ExecutorCore implements Listener {

    private final BungeeSupport bungeeSupport = BungeeSupport.INSTANCE;
    private final Map<String, User> map = Account.DEFAULT.getUserMap();
    private final Messenger messenger;
    private final Main main;
    private final EbeanServer db;
    private final boolean mail;

    public ExecutorCore(Main main, Messenger messenger) {
        this.messenger = messenger;
        this.db = main.getDatabase();
        this.main = main;
        mail = main.getConfig().getBoolean("notify.mail");
    }

    public void bind() {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (main.isMinimal() || !bungeeSupport.hasLoggedIn(p)) {
            processJoin(p);
        }
    }

    private void processJoin(Player p) {
        main.execute(() -> {
            User user = db.find(User.class)
                    .where()
                    .eq("username", p.getName())
                    .findUnique();
            if (user == null) {
                map.put(p.getName(), db.createEntityBean(User.class));
            } else {
                if (mail && (user.getEmail() == null || user.getEmail().equals(""))) {
                    messenger.send(p, "notify.mail", ChatColor.GOLD + "帐号安全请尽快前往论坛绑定邮箱");
                }
                main.process(() -> {
                    post(new UserFetchedEvent(p, user));
                });
                map.put(p.getName(), user);
            }
        });
    }

    @EventHandler
    public void handle(PlayerQuitEvent event) {
        map.remove(event.getPlayer().getName());
    }

    private void post(UserFetchedEvent event) {
        main.getServer().getPluginManager().callEvent(event);
    }

}
