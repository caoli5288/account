package com.mengcraft.account.bungee;

import com.mengcraft.account.LockedList;
import com.mengcraft.account.util.ReadWriteUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.DataInput;
import java.util.HashMap;
import java.util.Map;

import static com.mengcraft.account.util.Util.eq;

/**
 * Created on 16-2-17.
 */
public class BungeeSupport implements PluginMessageListener {

    public static final BungeeSupport INSTANCE = new BungeeSupport();

    private BungeeSupport() {
    }

    private final Map<String, String> map = new HashMap<>();

    @Override
    public void onPluginMessageReceived(String tag, Player p, byte[] data) {
        if (eq(tag, BungeeMessage.CHANNEL)) {
            processMessage(data);
        }
    }

    private void processMessage(byte[] data) {
        DataInput input = ReadWriteUtil.toDataInput(data);
        BungeeMessage message = BungeeMessage.read(input);
        if (eq(message.getType(), BungeeMessage.ADD)) {
            Player p = Bukkit.getPlayerExact(message.getName());
            if (!eq(p, null) && LockedList.INSTANCE.isLocked(p.getUniqueId())) {
                if (eq(message.getIp(), p.getAddress().getAddress().getHostAddress())) {
                    LockedList.INSTANCE.remove(p.getUniqueId());
                }
            }
            map.put(message.getName(), message.getIp());
        } else if (eq(message.getType(), BungeeMessage.DEL)) {
            map.remove(message.getName());
        }
    }

    public boolean hasLoggedIn(Player p) {
        String ip = map.get(p.getName());
        return ip != null && eq(ip, p.getAddress().getAddress().getHostAddress());
    }

    public void sendLoggedIn(Plugin plugin, Player p) {
        BungeeMessage message = new BungeeMessage();
        message.setType(BungeeMessage.DISTRIBUTE);
        message.setName(p.getName());
        message.setIp(p.getAddress().getAddress().getHostAddress());
        plugin.getLogger().info("Send " + message.toString());
        p.sendPluginMessage(plugin, BungeeMessage.CHANNEL, message.toByteArray());
    }

}
