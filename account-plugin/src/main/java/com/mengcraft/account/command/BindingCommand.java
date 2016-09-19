package com.mengcraft.account.command;

import com.avaje.ebean.EbeanServer;
import com.mengcraft.account.Account;
import com.mengcraft.account.Main;
import com.mengcraft.account.entity.AppAccountBinding;
import com.mengcraft.account.entity.Member;
import com.mengcraft.account.util.It;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.Proxy;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.mengcraft.account.util.Util.eq;

/**
 * Created on 16-7-8.
 */
public class BindingCommand implements CommandExecutor {

    private final Set<String> locked = new HashSet<>();
    private final Main main;

    public BindingCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender p, Command cmd, String s, String[] j) {
        if (p instanceof Player) {
            return execute(((Player) p), new It<>(j));
        }
        return false;
    }

    private boolean execute(Player p, It<String> it) {
        if (it.hasNext()) {
            if (eq(it.length(), 2)) {
                return execute(p, it.next(), it.next());
            } else {
                p.sendMessage(ChatColor.GRAY + "输入\"/正版绑定 正版账号 正版密码\"进行绑定");
            }
        } else {
            Member member = Account.INSTANCE.getMember(p);
            if (eq(member, null)) {
                p.sendMessage(ChatColor.GRAY + "账号数据正在获取，请稍后再尝试");
            } else {
                return fetchInfo(p, member);
            }
        }
        return false;
    }

    private boolean fetchInfo(Player p, Member member) {
        AppAccountBinding binding = member.getBinding();
        if (eq(binding, null)) {
            p.sendMessage(ChatColor.GRAY + "您未绑定正版账号，输入\"/正版绑定 正版账号 正版密码\"进行绑定");
        } else {
            p.sendMessage(ChatColor.GOLD + "您已绑定正版账号：" + binding.getBinding());
        }
        return true;
    }

    private boolean execute(Player p, String name, String pass) {
        if (Account.INSTANCE.memberBinding(p)) {
            p.sendMessage(ChatColor.GOLD + "您已绑定正版账号，请勿重复绑定");
        } else {
            return processPreQuery(p, name, pass);
        }
        return false;
    }

    private boolean processPreQuery(Player p, String name, String pass) {
        if (locked.add(p.getName())) {
            main.execute(() -> {
                EbeanServer db = main.getDatabase();
                AppAccountBinding j1 = db.find(AppAccountBinding.class)
                        .where()
                        .eq("binding", name)
                        .findUnique();
                if (eq(j1, null)) {
                    processQuery(p, name, pass);
                } else {
                    p.sendMessage(ChatColor.RED + "该账号已被玩家"
                            + db.find(Member.class, j1.getUid()).getUsername()
                            + "绑定"
                    );
                }
                main.process(() -> locked.remove(p.getName()));
            });
            return true;
        }
        return false;
    }

    private void processQuery(Player p, String mail, String pass) {
        YggdrasilUserAuthentication remote = new YggdrasilUserAuthentication(new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString()), Agent.MINECRAFT);
        remote.setUsername(mail);
        remote.setPassword(pass);
        try {
            remote.logIn();
            if (remote.canPlayOnline()) {
                execute(p, Account.INSTANCE.getMember(p), mail, remote.getSelectedProfile().getId());
            } else {
                p.sendMessage(ChatColor.RED + "发生了一些问题，认证出错或正版验证服务器无法连接");
            }
        } catch (Exception ignored) {
            p.sendMessage(ChatColor.RED + "发生了一些问题，认证出错或正版验证服务器无法连接");
        }
    }

    private void execute(Player p, Member member, String name, UUID id) {
        EbeanServer db = main.getDatabase();
        db.beginTransaction();
        try {
            AppAccountBinding binding = new AppAccountBinding();
            binding.setBinding(name);
            binding.setBindingId(id);
            binding.setMember(member);
            member.setBinding(binding);
            db.save(member);
            db.commitTransaction();
            p.sendMessage(ChatColor.GOLD + "正版账号绑定成功");
            main.process(() -> {
                callback(main.getServer().getConsoleSender(), main.getConfig().getString("binding.execute"), p.getName());
            });
        } finally {
            db.endTransaction();
        }
    }

    private void callback(CommandSender sender, String string, String name) {
        for (String line : string.replace("$p", name).split(";")) {
            main.getServer().dispatchCommand(sender, line);
        }
    }

}
