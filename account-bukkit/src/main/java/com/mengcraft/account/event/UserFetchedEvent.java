package com.mengcraft.account.event;

import com.mengcraft.account.entity.User;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created on 16-2-17.
 */
public class UserFetchedEvent extends Event {

    public static final HandlerList HANDLER_LIST = new HandlerList();
    private final User user;
    private final Player player;

    public UserFetchedEvent(Player player, User user) {
        this.player = player;
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return user.getUsername();
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

}
