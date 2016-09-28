package com.mengcraft.account.command;

import com.avaje.ebean.EbeanServer;
import com.mengcraft.account.Account;
import com.mengcraft.account.Main;
import com.mengcraft.account.entity.AppAccountBinding;
import com.mengcraft.account.entity.Member;
import com.mengcraft.account.util.It;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilUserAuthentication;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.Proxy;
import java.util.HashSet;
import java.util.List;
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
        boolean b = Account.INSTANCE.memberBinding(p);
        if (b) {
            p.sendMessage(ChatColor.GOLD + "您已绑定正版账号，请勿重复绑定");
        } else {
            processPreQuery(p, name, pass);
        }
        return !b;
    }

    private void processPreQuery(Player p, String mail, String pass) {
        if (locked.add(p.getName())) {
            main.execute(() -> {
                YggdrasilUserAuthentication remote = new YggdrasilUserAuthentication(new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString()), Agent.MINECRAFT);
                remote.setUsername(mail);
                remote.setPassword(pass);
                try {
                    remote.logIn();
                    if (remote.canPlayOnline()) {
                        execute(p, Account.INSTANCE.getMember(p), mail, remote.getSelectedProfile());
                    } else {
                        p.sendMessage(ChatColor.RED + "发生了一些问题，认证出错或正版验证服务器无法连接");
                    }
                } catch (Exception ignored) {
                    p.sendMessage(ChatColor.RED + "该账号无法绑定");
                }
                main.process(() -> locked.remove(p.getName()));
            });
        } else {
            p.sendMessage(ChatColor.RED + "验证您的账号中，请稍候");
        }
    }

    private void execute(Player p, Member member, String mail, GameProfile profile) {
        EbeanServer db = main.getDatabase();
        AppAccountBinding binding = new AppAccountBinding();
        binding.setBinding(mail);
        binding.setBindingId(profile.getId());
        binding.setMember(member);
        db.save(binding);
        db.refresh(member);// Force refresh from db
        p.sendMessage(ChatColor.GOLD + "正版账号绑定成功");
        main.process(() -> {
            execute(main.getConfig().getStringList("binding.execute"), p.getName());
        });
    }

    private void execute(List<String> list, String name) {
        CommandSender sender = main.getServer().getConsoleSender();
        for (String line : list) {
            main.getServer().dispatchCommand(sender, line.replace("%player%", name));
        }
    }

}
