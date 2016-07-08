package com.mengcraft.account.command;

import com.avaje.ebean.EbeanServer;
import com.mengcraft.account.Account;
import com.mengcraft.account.Main;
import com.mengcraft.account.entity.AppAccountBinding;
import com.mengcraft.account.entity.User;
import com.mojang.authlib.Agent;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

import static com.mengcraft.account.Main.eq;

/**
 * Created on 16-7-8.
 */
public class BindingCommand implements CommandExecutor {

    private final Set<String> locked = new HashSet<>();
    private final Main main;
    private final Account account = Account.DEFAULT;

    public BindingCommand(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender p, Command cmd, String s, String[] j) {
        if (p instanceof Player) {
            return execute(((Player) p), Arrays.asList(j).iterator());
        }
        return false;
    }

    private boolean execute(Player p, Iterator<String> it) {
        if (it.hasNext()) {
            String name = it.next();
            if (it.hasNext()) {
                String pass = it.next();
                if (it.hasNext()) {
                    p.sendMessage(ChatColor.GRAY + "输入\"/正版绑定 正版账号 正版密码\"进行绑定");
                } else {
                    return execute(p, name, pass);
                }
            } else {
                p.sendMessage(ChatColor.GRAY + "输入\"/正版绑定 正版账号 正版密码\"进行绑定");
            }
        } else {
            User user = account.getUser(p);
            if (eq(user, null)) {
                p.sendMessage(ChatColor.GRAY + "账号数据正在获取，请稍后再尝试");
            } else {
                AppAccountBinding binding = user.getBinding();
                if (eq(binding, null)) {
                    p.sendMessage(ChatColor.GRAY + "您未绑定正版账号，输入\"/正版绑定 正版账号 正版密码\"进行绑定");
                } else {
                    p.sendMessage(ChatColor.GOLD + "您已绑定正版账号：" + binding.getBinding());
                }
                return true;
            }
        }
        return false;
    }

    private boolean execute(Player p, String name, String pass) {
        User user = account.getUser(p);
        if (eq(user, null)) {
            p.sendMessage(ChatColor.GRAY + "账号数据正在获取，请稍后再尝试");
        } else {
            AppAccountBinding binding = user.getBinding();
            if (eq(binding, null) && locked.add(p.getName())) {
                main.execute(() -> {
                    YggdrasilUserAuthentication remote = new YggdrasilUserAuthentication(new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString()), Agent.MINECRAFT);
                    remote.setUsername(name);
                    remote.setPassword(pass);
                    try {
                        remote.logIn();
                        if (remote.canPlayOnline()) {
                            execute(p, user, name);
                        } else {
                            p.sendMessage(ChatColor.RED + "发生了一些问题，认证出错或正版验证服务器无法连接");
                        }
                    } catch (Exception ignored) {
                        p.sendMessage(ChatColor.RED + "发生了一些问题，认证出错或正版验证服务器无法连接");
                    }
                    main.process(() -> locked.remove(p.getName()));
                });
                return true;
            } else {
                p.sendMessage(ChatColor.GOLD + "您已绑定正版账号，请勿重复绑定");
            }
        }
        return false;
    }

    private void execute(Player p, User user, String name) {
        EbeanServer db = main.getDatabase();
        db.beginTransaction();
        try {
            AppAccountBinding dup = db.find(AppAccountBinding.class)
                    .where()
                    .eq("binding", name)
                    .findUnique();
            if (eq(dup, null)) {
                AppAccountBinding binding = new AppAccountBinding();
                binding.setBinding(name);
                binding.setUser(user);
                user.setBinding(binding);
                db.save(user);
                db.commitTransaction();
                p.sendMessage(ChatColor.GOLD + "正版账号绑定成功");
                main.process(() -> {
                    main.getServer().dispatchCommand(main.getServer().getConsoleSender(), String.format(main.getConfig().getString("binding.execute"), p.getName()));
                });
            } else {
                p.sendMessage(ChatColor.GOLD + "该正版账号已绑定：" + dup.getUser().getUsername());
            }
        } finally {
            db.endTransaction();
        }
    }
}
