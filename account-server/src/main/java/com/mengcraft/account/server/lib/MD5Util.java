package com.mengcraft.account.server.lib;

import java.security.MessageDigest;

/**
 * Created by on 16-4-14.
 */
public class MD5Util {

    private static final char[] HEX_ARRAY = {
            '0', '1', '2', '3', '4', '5',
            '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f'
    };

    public String digest(String in) throws Exception {
        if (in == null) {
            throw new NullPointerException();
        }
        return digest(in.getBytes());
    }

    public String digest(byte[] in) throws Exception {
        if (in == null) {
            throw new NullPointerException();
        }
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(in);
        byte[] out = md.digest();
        return hex(out);
    }

    public String hex(byte[] out) {
        if (out == null) {
            throw new NullPointerException();
        }
        StringBuilder buf = new StringBuilder();
        for (byte b : out) {
            buf.append(HEX_ARRAY[b >>> 4 & 0xf]);
            buf.append(HEX_ARRAY[b & 0xf]);
        }
        return buf.toString();
    }

}
