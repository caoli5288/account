package com.mengcraft.account;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by on 16-4-22.
 */
public class LockedList {

    public static final LockedList INSTANCE = new LockedList();

    private final Set<UUID> locked = new HashSet<>();

    public Collection<UUID> getLocked() {
        return locked;
    }

    public boolean add(UUID uuid) {
        return locked.add(uuid);
    }

    public boolean remove(UUID uuid) {
        return locked.remove(uuid);
    }

    public boolean isLocked(UUID uuid) {
        return locked.contains(uuid);
    }

}
