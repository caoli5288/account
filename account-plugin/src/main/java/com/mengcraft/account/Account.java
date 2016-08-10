package com.mengcraft.account;

import com.mengcraft.account.entity.Member;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Account {

    public static final Account INSTANCE = new Account();

    private final Map<String, Member> handle;
    private Main main;

    private Account() {
        this.handle = new ConcurrentHashMap<>();
    }

    public int getMemberKey(String name) {
        return getMember(name).getUid();
    }

    public int getMemberKey(Player p) {
        return getMemberKey(p.getName());
    }

    public void drop(String name) {
        handle.remove(name);
    }

    public Member getMember(String name) {
        Member j = handle.get(name);
        if (Main.eq(j, null)) {
            j = fetch(name);
            handle.put(name, j);
        }
        return j;
    }

    private Member fetch(String name) {
        Member member = main.getDatabase().find(Member.class)
                .where()
                .eq("username", name)
                .findUnique();
        if (Main.eq(member, null)) {
            member = new Member();
        }
        return member;
    }

    public Member getMember(Player p) {
        return getMember(p.getName());
    }

    public void setMain(Main main) {
        this.main = main;
    }

}
