package com.mengcraft.account;

import com.avaje.ebean.EbeanServer;
import com.mengcraft.account.entity.User;
import com.mengcraft.account.event.UserFetchedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Map;

public class ExecutorCore implements Listener {

    private final Map<String, User> userMap = Account.DEFAULT.getUserMap();
    private final Main main;
    private final EbeanServer db;

    public ExecutorCore(Main main) {
        this.db = main.getDatabase();
        this.main = main;
    }

    public void bind() {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        main.execute(() -> {
            User user = db.find(User.class)
                    .where()
                    .eq("username", player.getName())
                    .findUnique();
            if (user == null) {
                getUserMap().put(player.getName(), db.createEntityBean(User.class));
            } else {
                getUserMap().put(player.getName(), user);
                main.execute(() -> {
                    callEvent(new UserFetchedEvent(player, user));
                }, false);
            }
        }, true);
    }

    @EventHandler
    public void handle(PlayerQuitEvent event) {
        getUserMap().remove(event.getPlayer().getName());
    }

    private void callEvent(UserFetchedEvent event) {
        main.getServer().getPluginManager().callEvent(event);
    }

    public Map<String, User> getUserMap() {
        return userMap;
    }

}
