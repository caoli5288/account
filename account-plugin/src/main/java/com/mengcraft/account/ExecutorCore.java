package com.mengcraft.account;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class ExecutorCore implements Listener {

    private final Main main;

    public ExecutorCore(Main main) {
        this.main = main;
    }

    public void bind() {
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void handle(PlayerQuitEvent event) {
        Account.INSTANCE.drop(event.getPlayer().getName());
    }

}
