package com.mengcraft.account.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Created on 17-1-18.
 */
public class $ {

    public static class Runner extends BukkitRunnable implements ICancellable {

        private final IRunner r;

        Runner(IRunner r) {
            this.r = r;
        }

        @Override
        public void run() {
            r.run(this);
        }
    }

    public interface ICancellable {

        void cancel();
    }

    public interface IRunner {

        void run(ICancellable r);
    }

    public static int run(Plugin plugin, int i, int repeat, IRunner r) {
        Runner out = new Runner(r);
        if (i > 0) {
            if (repeat > 0) {
                out.runTaskTimer(plugin, i, repeat);
            } else {
                out.runTaskLater(plugin, i);
            }
        } else {
            out.runTask(plugin);
        }
        return out.getTaskId();
    }

    public static boolean nil(Object ob) {
        return ob == null;
    }

    public static boolean eq(Object left, Object o) {
        return (left == o) || (!nil(left) && left.equals(o));
    }

}
