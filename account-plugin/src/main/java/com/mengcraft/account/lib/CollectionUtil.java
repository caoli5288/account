package com.mengcraft.account.lib;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created on 16-3-9.
 */
public final class CollectionUtil {

    public static <T> Collection<T> convertTo(Collection<T> in, Predicate<T> p) {
        List<T> out = new ArrayList<>();
        forEach(in, p, t -> {
            out.add(t);
        });
        return out;
    }

    public static <T> void forEach(Collection<T> in, Predicate<T> p, Consumer<T> c) {
        in.forEach(t -> {
            if (p.test(t)) {
                c.accept(t);
            }
        });
    }

    public static <T> void forEachRemaining(Iterator<T> in, Predicate<T> p, Consumer<T> c) {
        in.forEachRemaining(t -> {
            if (p.test(t)) {
                c.accept(t);
            }
        });
    }

}
