package com.mengcraft.account;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by on 16-4-22.
 */
public class LockedList {

    public static final LockedList INSTANCE = new LockedList();

    private final List<UUID> locked = new ArrayList<>();

    public List<UUID> getLocked() {
        return locked;
    }

    public void add(UUID uuid) {
        locked.add(uuid);
    }

    public void remove(UUID uuid) {
        locked.remove(uuid);
    }

    public boolean isLocked(UUID uuid) {
        return locked.contains(uuid);
    }

}
