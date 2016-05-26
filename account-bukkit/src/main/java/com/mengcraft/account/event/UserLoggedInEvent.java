package com.mengcraft.account.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created on 16-3-6.
 */
public class UserLoggedInEvent extends Event {

    public static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player player;

    public UserLoggedInEvent(Player player) {
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public Player getPlayer() {
        return player;
    }

    public static void post(Player player) {
        player.getServer().getPluginManager().callEvent(new UserLoggedInEvent(player));
    }

}
