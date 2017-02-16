package com.mengcraft.account;

import com.mengcraft.account.entity.Member;
import com.mengcraft.account.util.$;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.mengcraft.account.Main.nil;

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

    void drop(String name) {
        handle.remove(name);
    }

    public Member getMember(String name) {
        Member j = handle.get(name);
        if (nil(j)) {
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
        if ($.nil(member)) {
            member = new Member();
        }
        return member;
    }

    public boolean memberFetched(String name) {
        return handle.containsKey(name);
    }

    public boolean memberFetched(Player p) {
        return handle.containsKey(p.getName());
    }

    public boolean memberBinding(Player p) {
        return memberBinding(p.getName());
    }

    public boolean memberBinding(String name) {
        Member member = getMember(name);
        return member != null && member.getBinding() != null;
    }

    public Member getMember(Player p) {
        return getMember(p.getName());
    }

    public void setMain(Main main) {
        this.main = main;
    }

}
