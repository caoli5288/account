package com.mengcraft.account.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

/**
 * Created on 16-4-13.
 */
public class Messenger {

    private final static String PREFIX = "message.";
    private final Plugin main;

    public Messenger(Plugin main) {
        this.main = main;
    }

    public void send(CommandSender p, String path) {
        send(p, path, null);
    }

    public void send(CommandSender p, String path, String def) {
        sendMessage(p, find(path, def));
    }

    public String find(String path) {
        return find(path, null);
    }

    public String find(String path, String def) {
        String found = main.getConfig().getString(with(path));
        if (found == null) {
            if (def == null) {
                return path;
            } else {
                main.getConfig().set(with(path), def);
                main.saveConfig();
            }
            return def;
        }
        return found;
    }

    private void sendMessage(CommandSender p, String text) {
        p.sendMessage(ChatColor.translateAlternateColorCodes('&', text));
    }

    private String with(String str) {
        return PREFIX + str;
    }

}
