package com.mengcraft.account;

import com.mengcraft.account.lib.ReadWriteUtil;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.mengcraft.account.bungee.BungeeMain.CHANNEL;

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
        DataInput input = ReadWriteUtil.toDataInput(data);
        try {
            byte b = input.readByte();
            if (b == 1) {
                String name = input.readUTF();
                String ip = input.readUTF();
                map.put(name, ip);
                OfflinePlayer j = Bukkit.getOfflinePlayer(name);
                if (ExecutorLocked.INSTANCE.isLocked(j.getUniqueId()) && j.isOnline()) {
                    Player i = j.getPlayer();
                    if (Main.eq(ip, i.getAddress().getAddress().getHostAddress())) {
                        ExecutorLocked.INSTANCE.remove(j.getUniqueId());
                    }
                }
            } else if (b == 2) {
                map.remove(input.readUTF());
            } else {
                throw new IllegalArgumentException(String.valueOf(b));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasLoggedIn(Player p) {
        String ip = map.get(p.getName());
        return ip != null && Main.eq(ip, p.getAddress().getAddress().getHostAddress());
    }

    public void sendLoggedIn(Plugin plugin, Player p) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutput output = ReadWriteUtil.toDataOutput(buf);
        try {
            output.writeByte(0);
            output.writeUTF(p.getName());
            output.writeUTF(p.getAddress().getAddress().getHostAddress());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        p.sendPluginMessage(plugin, CHANNEL, buf.toByteArray());
    }

}
