package com.mengcraft.account.event;

import com.mengcraft.account.entity.Member;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Created on 16-3-6.
 */
public class UserLoggedInEvent extends Event {

    public static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final Member member;

    private UserLoggedInEvent(Player player, Member member) {
        this.player = player;
        this.member = member;
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

    public Member getMember() {
        return member;
    }

    public static UserLoggedInEvent call(Player player, Member member) {
        UserLoggedInEvent event = new UserLoggedInEvent(player, member);
        player.getServer().getPluginManager().callEvent(event);
        return event;
    }

}
