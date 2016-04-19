package com.mengcraft.account.entity;

import com.avaje.ebean.annotation.CreatedTimestamp;
import org.bukkit.entity.Player;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

/**
 * Created by on 15-10-10.
 */
@Entity
public class AppAccountEvent {

    public static final int REG_SUCCESS = 0;
    public static final int REG_FAILURE = 1;
    public static final int LOG_SUCCESS = 2;
    public static final int LOG_FAILURE = 3;

    @Id
    private int id;

    @Column
    private String name;

    @Column
    private String ip;

    @Column
    private int type;

    @CreatedTimestamp
    private Timestamp time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public static AppAccountEvent of(Player player, int type) {
        AppAccountEvent event = new AppAccountEvent();
        event.name = player.getName();
        event.ip = player.getAddress().getAddress().getHostAddress();
        event.type = type;

        return event;
    }

}
