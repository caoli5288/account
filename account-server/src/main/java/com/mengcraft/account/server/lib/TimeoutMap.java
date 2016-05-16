package com.mengcraft.account.server.lib;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created on 16-4-20.
 */
public class TimeoutMap extends ConcurrentHashMap<String, Long> {

    private final long time;

    /**
     * @param time Time in millis.
     */
    public TimeoutMap(long time) {
        this.time = time;
    }

    public void put(String key) {
        put(key, System.currentTimeMillis());
    }

    public boolean isOnTime(String key) {
        return (containsKey(key) &&
                get(key) + time > System.currentTimeMillis()
        );
    }

}
