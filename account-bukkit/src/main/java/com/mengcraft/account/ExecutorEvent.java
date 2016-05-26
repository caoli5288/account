package com.mengcraft.account;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;

import static com.mengcraft.account.lib.CollectionUtil.convertTo;

/**
 * Created on 15-10-26.
 */
public class ExecutorEvent implements Listener {

    private final ExecutorLocked locked = ExecutorLocked.INSTANCE;

    @EventHandler(priority = EventPriority.MONITOR)
    public void handle(PlayerLoginEvent event) {
        if (event.getResult() == PlayerLoginEvent.Result.ALLOWED) {
            locked.add(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void handle(PlayerMoveEvent event) {
        if (locked.isLocked(event.getPlayer().getUniqueId())) {
            Location from = event.getFrom();
            from.setPitch(event.getTo().getPitch());
            from.setYaw(event.getTo().getYaw());

            event.setTo(from);
        }
    }

    @EventHandler
    public void handle(PlayerInteractEvent event) {
        if (locked.isLocked(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handle(PlayerDropItemEvent event) {
        if (locked.isLocked(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handle(PlayerPickupItemEvent event) {
        if (locked.isLocked(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handle(PlayerQuitEvent event) {
        if (locked.isLocked(event.getPlayer().getUniqueId())) {
            locked.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void handle(InventoryOpenEvent event) {
        if (locked.isLocked(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void handle(EntityDamageEvent event) {
        if (a(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void handle(EntityDamageByEntityEvent event) {
        if (a(event.getDamager())) {
            event.setCancelled(true);
        }
    }

    private boolean a(Entity entity) {
        return entity instanceof Player && locked.isLocked(entity.getUniqueId());
    }

    @EventHandler
    public void handle(FoodLevelChangeEvent event) {
        if (locked.isLocked(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handle(AsyncPlayerChatEvent event) {
        if (locked.isLocked(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        } else {
            excludeLocked(event.getRecipients());
        }
    }

    private void excludeLocked(Set<Player> set) {
        set.removeAll(convertTo(set, p -> locked.isLocked(p.getUniqueId())));
    }

    @EventHandler
    public void handle(PlayerCommandPreprocessEvent event) {
        if (locked.isLocked(event.getPlayer().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    public void bind(Main main) {
        main.getServer().getPluginManager().registerEvents(this, main);
    }
}
