package com.mengcraft.account.server.lib;

import java.util.Random;

/**
 * Created by on 16-4-14.
 */
public class SessionBuilder extends Random {

    private static final char[] ARRAY_CHAR = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890".toCharArray();
    private static final int ARRAY_LENGTH = 62;

    public String nextSession() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 32; i++) {
            builder.append(ARRAY_CHAR[nextInt(ARRAY_LENGTH)]);
        }
        return builder.toString();
    }

}
