package com.mengcraft.account;

import com.avaje.ebean.EbeanServer;
import com.mengcraft.account.bungee.BungeeSupport;
import com.mengcraft.account.entity.AppAccountBinding;
import com.mengcraft.account.entity.AppAccountEvent;
import com.mengcraft.account.entity.Member;
import com.mengcraft.account.event.UserLoggedInEvent;
import com.mengcraft.account.util.$;
import com.mengcraft.account.util.Messenger;
import com.mengcraft.account.util.SecureUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.mengcraft.account.entity.AppAccountEvent.LOG_FAILURE;
import static com.mengcraft.account.entity.AppAccountEvent.LOG_SUCCESS;
import static com.mengcraft.account.entity.AppAccountEvent.of;

public class Executor implements Listener {

    private final Main main;
    private final Messenger messenger;
    private final EbeanServer db;

    private final List<Pattern> disallowed;
    private final boolean regDisabled;

    public Executor(Main main, Messenger messenger) {
        this.messenger = messenger;
        this.main = main;
        db = main.getDatabase();
        regDisabled = main.getConfig().getBoolean("register.disable");
        HashSet<String> disallow = new HashSet<>(main.getConfig().getStringList("register.disallow"));
        disallowed = new ArrayList<>(disallow.size());
        for (String i : disallow) {
            disallowed.add(Pattern.compile(i));
        }
    }

    private String[] contents;
    private int castInterval;

    public void addExecLogin() {
        $.IExec exec = this::processPreLogin;
        $.addExecutor(main, "l", exec);
        $.addExecutor(main, "login", exec);
    }

    public void addExecRegister() {
        $.IExec exec = this::preRegister;
        $.addExecutor(main, "r", exec);
        $.addExecutor(main, "reg", exec);
        $.addExecutor(main, "register", exec);
    }

    @EventHandler
    public void handle(AsyncPlayerPreLoginEvent event) {
        if (event.getName().length() > 15) {
            event.setLoginResult(Result.KICK_OTHER);
            event.setKickMessage(messenger.find("login.length", "用户名长度不能大于15位"));
        } else if (!event.getName().matches("[\\w]+")) {
            event.setLoginResult(Result.KICK_OTHER);
            event.setKickMessage(messenger.find("login.except", "用户名只能包含英文数字下划线"));
        }
    }

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        if (BungeeSupport.INSTANCE.hasLoggedIn(p)) {
            LockedList.INSTANCE.remove(p.getUniqueId());
        } else {
            $.run(main, 20, castInterval, t -> {
                if (p.isOnline() && isLocked(p.getUniqueId()))
                    p.sendMessage(contents);
                else
                    t.cancel(); // Cancel if p exit or unlocked.
            });
            main.run(() -> {
                if (p.isOnline() && isLocked(p.getUniqueId())) {
                    event.getPlayer().kickPlayer(messenger.find("login.kick", ChatColor.DARK_RED + "未登录"));
                    if (main.isLog()) {
                        main.execute(() -> db.save(of(p, LOG_FAILURE)));
                    }
                }
            }, main.getConfig().getInt("kick", 600));
        }
    }

    @EventHandler
    public void handle(PlayerQuitEvent event) {
        Account.INSTANCE.drop(event.getPlayer().getName());
    }

    public void setCastInterval(int castInterval) {
        this.castInterval = castInterval;
    }

    public void bind() {
        setContents(main.getConfig().getStringList("broadcast.content"));
        getMain().getServer()
                .getPluginManager()
                .registerEvents(this, main);
        setCastInterval(main.getConfig().getInt("broadcast.interval"));
    }

    private boolean preRegister(CommandSender sender, String unneeded, List<String> list) {
        if (sender instanceof Player && list.size() == 2) {
            Player p = (Player) sender;
            if (!isLocked(p.getUniqueId())) return false;
            register(p, list.get(0), list.get(1));
            return true;
        } else {
            messenger.send(sender, "register.format", ChatColor.DARK_RED + "输入/register <密码> <重复密码>以完成注册");
        }
        return false;
    }

    private void register(Player p, String pass, String next) {
        main.execute(() -> {
            Member member = Account.INSTANCE.getMember(p);
            if (member.valid()) {
                messenger.send(p, "register.failure", ChatColor.DARK_RED + "注册失败，本用户已经注册过");
            } else if (regDisabled) {
                messenger.send(p, "register.disable", ChatColor.DARK_RED + "注册失败，服务器已关闭注册");
            } else if (disallowed(p.getName())) {
                messenger.send(p, "register.disallow", ChatColor.DARK_RED + "注册失败，本用户名不被允许");
            } else if (pass.length() < 6) {
                messenger.send(p, "register.password.short", ChatColor.DARK_RED + "注册失败，请使用6位长度以上的密码");
            } else if (!$.eq(pass, next)) {
                messenger.send(p, "register.password.equal", ChatColor.DARK_RED + "注册失败，两次输入的密码内容不一致");
            } else {
                init(p, pass, member);
            }
        });
    }

    private boolean disallowed(String name) {
        for (Pattern p : disallowed) {
            if (p.matcher(name).matches()) {
                return true;
            }
        }
        return false;
    }

    private void init(Player p, String pass, Member member) {
        SecureUtil util = SecureUtil.DEFAULT;
        String salt = util.random(3);

        try {
            member.setPassword(util.digest(util.digest(pass) + salt));
        } catch (Exception e) {
            throw new RuntimeException("init", e);
        }

        member.setSalt(salt);
        member.setUsername(p.getName());
        member.setRegip(p.getAddress().getAddress().getHostAddress());
        member.setRegdate(now());
        member.setEmail("");

        db.save(member); //May throw exception.
        if (main.isLog()) {
            db.save(of(p, AppAccountEvent.REG_SUCCESS));
            db.save(of(p, LOG_SUCCESS));
        }

        BungeeSupport.INSTANCE.sendLoggedIn(main, p);

        main.run(() -> { // Thread safe
            LockedList.INSTANCE.remove(p.getUniqueId());
        });

        messenger.send(p, "register.succeed", ChatColor.GREEN + "注册成功");
    }

    private boolean processPreLogin(CommandSender who, String unneeded, List<String> l) {
        if (who instanceof Player) {
            Player p = (Player) who;
            if (l.isEmpty() || !isLocked(p.getUniqueId())) return false;
            main.execute(() -> processLogin(p, l.get(0)));
            return true;
        }
        return false;
    }

    private void processLogin(Player p, String password) {// Need threading
        Member member = Account.INSTANCE.getMember(p);
        if (member.valid() && member.valid(password)) {
            BungeeSupport.INSTANCE.sendLoggedIn(main, p);

            main.run(() -> {// Thread safe
                LockedList.INSTANCE.remove(p.getUniqueId());
                UserLoggedInEvent.call(p, member);
            });
            if (main.isLog()) {
                db.save(of(p, LOG_SUCCESS));
            }

            // Post login

            messenger.send(p, "login.done", ChatColor.GREEN + "登录成功");
            if (member.getEmail().isEmpty() && main.notifyMail()) {
                messenger.send(p, "notify.mail", ChatColor.RED + "为了您的账号安全请尽快前往论坛用户中心绑定密保邮箱");
            }

            AppAccountBinding binding = member.getBinding();
            if (!Main.nil(binding) && !p.getName().equals(binding.getName())) {
                binding.setName(p.getName());
                main.getDatabase().save(binding);
            }
        } else {
            messenger.send(p, "login.password", ChatColor.DARK_RED + "密码错误");
        }
    }

    private int now() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    private boolean isLocked(UUID uuid) {
        return LockedList.INSTANCE.isLocked(uuid);
    }

    private void setContents(List<String> list) {
        contents = list.toArray(new String[list.size()]);
    }

    private Main getMain() {
        return main;
    }

}
