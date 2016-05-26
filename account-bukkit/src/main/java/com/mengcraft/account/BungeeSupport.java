package com.mengcraft.account;

import com.mengcraft.account.lib.ReadWriteUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.mengcraft.account.Main.eq;

/**
 * Created on 16-2-17.
 */
public class BungeeSupport implements PluginMessageListener {

    public static final BungeeSupport INSTANCE = new BungeeSupport();
    private final Map<String, String> map = new HashMap<>();

    private BungeeSupport() {
    }

    @Override
    public void onPluginMessageReceived(String tag, Player p, byte[] data) {
        DataInput input = ReadWriteUtil.toDataInput(data);
        try {
            byte b = input.readByte();
            if (b == 1) {
                map.put(input.readUTF(), input.readUTF());
            } else if (b == 2) {
                map.remove(input.readUTF());
            } else {
                throw new IllegalArgumentException(String.valueOf(b));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    public static final String CHANNEL = "AccountBungeeSession";

}
