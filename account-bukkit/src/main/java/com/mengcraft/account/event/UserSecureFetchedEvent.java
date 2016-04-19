package com.mengcraft.account.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created on 16-2-17.
 */
public class UserSecureFetchedEvent extends Event{

    private String name;
    private String secure;

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSecure(String secure) {
        this.secure = secure;
    }

    public String getName() {
        return name;
    }

    public String getSecure() {
        return secure;
    }

    public static final HandlerList HANDLER_LIST = new HandlerList();

}
