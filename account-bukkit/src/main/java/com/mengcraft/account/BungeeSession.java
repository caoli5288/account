package com.mengcraft.account;

import com.mengcraft.account.event.UserSecureFetchedEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;

/**
 * Created on 16-2-17.
 */
public class BungeeSession implements PluginMessageListener {

    private final Main main;

    public BungeeSession(Main main) {
        this.main = main;
    }

    @Override
    public void onPluginMessageReceived(String tag, Player player, byte[] data) {
        if (TAG.equals(tag)) {
            DataInputStream buf = new DataInputStream(new ByteArrayInputStream(data));
            {
                UserSecureFetchedEvent event = new UserSecureFetchedEvent();
                try {
                    event.setName(buf.readUTF());
                    event.setSecure(buf.readUTF());
                } catch (IOException e) {
                    main.getLogger().log(Level.WARNING, "", e);
                }
                main.getServer().getPluginManager().callEvent(event);
            }
        }
    }

    public static void sendSecure(Main main, Player p, String secure) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            DataOutputStream writer = new DataOutputStream(buf);
            writer.write(0);
            writer.writeUTF(p.getName());
            writer.writeUTF(secure);
        } catch (IOException e) {
            e.printStackTrace();
        }
        p.sendPluginMessage(main, TAG, buf.toByteArray());
    }

    public void bind() {
        main.getServer().getMessenger().registerOutgoingPluginChannel(main, TAG);
        main.getServer().getMessenger().registerIncomingPluginChannel(main, TAG, this);
    }

    public static final String TAG = "AccountBungeeSession";

}
