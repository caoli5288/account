package com.mengcraft.account.bungee;

import com.mengcraft.account.lib.ReadWriteUtil;
import net.md_5.bungee.api.config.ServerInfo;

import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;

/**
 * Created on 16-8-10.
 */
public class BungeeMessage {

    private byte type;
    private String name;
    private String ip;

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
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

    public boolean valid() {
        return name != null;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        DataOutput output = ReadWriteUtil.toDataOutput(buf);
        try {
            output.write(type);
            output.writeUTF(name);
            if (type < BungeeMain.DEL_LOGGED && ip != null) {
                output.writeUTF(ip);
            }
        } catch (IOException ignore) {
        }
        return buf.toByteArray();
    }

    public void broadcast(Collection<ServerInfo> targetList) {
        byte[] buf = toByteArray();
        for (ServerInfo info : targetList) {
            info.sendData(BungeeMain.CHANNEL, buf, true);
        }
    }

    @Override
    public String toString() {
        return ("message " +
                "type " + type + ", " +
                "name " + name + ", " +
                "ip " + ip
        );
    }

    public static BungeeMessage read(DataInput input) {
        BungeeMessage info = new BungeeMessage();
        try {
            info.type = input.readByte();
            info.name = input.readUTF();
            info.ip = input.readUTF();// Ignore this throw.
        } catch (IOException ignore) {
        }
        return info;
    }

}
