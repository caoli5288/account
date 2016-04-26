package com.mengcraft.account;

import com.avaje.ebean.EbeanServer;
import com.mengcraft.account.entity.AppAccountEvent;
import com.mengcraft.account.entity.User;
import com.mengcraft.account.event.UserFetchedEvent;
import com.mengcraft.account.event.UserSecureFetchedEvent;
import com.mengcraft.account.lib.ArrayVector;
import com.mengcraft.account.lib.Messenger;
import com.mengcraft.account.lib.SecureUtil;
import com.mengcraft.account.lib.StringUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.mengcraft.account.BungeeSession.TAG;
import static com.mengcraft.account.BungeeSession.sendSecure;
import static com.mengcraft.account.entity.AppAccountEvent.LOG_FAILURE;
import static com.mengcraft.account.entity.AppAccountEvent.LOG_SUCCESS;
import static com.mengcraft.account.entity.AppAccountEvent.of;
import static com.mengcraft.account.event.UserLoggedInEvent.postEvent;

public class Executor extends Messenger implements Listener {

    private final Map<String, User> userMap = Account.DEFAULT.getUserMap();
    private final Main main;
    private final EbeanServer db;
    private final LockedList locked = LockedList.INSTANCE;

    private String[] bungeeSessionMsg;

    public Executor(Main main) {
        super(main);
        this.main = main;
        this.db = main.getDatabase();
    }

    private String[] contents;
    private int castInterval;

    @EventHandler
    public void handle(PlayerCommandPreprocessEvent event) {
        if (isLocked(event.getPlayer().getUniqueId())) {
            String[] d = StringUtil.DEF.split(event.getMessage());
            ArrayVector<String> vector = new ArrayVector<>(d);
            String c = vector.next();
            if (c.equals("/l") || c.equals("/login")) {
                login(event.getPlayer(), vector);
            }
            if (c.equals("/r") || c.equals("/reg") || c.equals("/register")) {
                register(event.getPlayer(), vector);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void handle(AsyncPlayerPreLoginEvent event) {
        if (event.getName().length() > 15) {
            event.setLoginResult(Result.KICK_OTHER);
            event.setKickMessage(find("login.length", "用户名长度不能大于15位"));
        } else if (!event.getName().matches("[\\w]+")) {
            event.setLoginResult(Result.KICK_OTHER);
            event.setKickMessage(find("login.except", "用户名只能包含英文数字下划线"));
        }
    }

    @EventHandler
    public void handle(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        getScheduler().runTaskLater(getMain(), () -> {
            if (player.isOnline() && isLocked(player.getUniqueId())) {
                event.getPlayer().kickPlayer(find("login.kick", ChatColor.DARK_RED + "未登录"));
                if (main.isLogEvent()) main.execute(() -> {
                    db.save(of(player, LOG_FAILURE));
                }, true);
            }
        }, 600);
        new BukkitRunnable() {
            public void run() {
                if (player.isOnline() && isLocked(player.getUniqueId()))
                    player.sendMessage(contents);
                else
                    cancel(); // Cancel if player exit or unlocked.
            }
        }.runTaskTimer(main, 20, castInterval);
    }

    @EventHandler
    public void handle(UserFetchedEvent event) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        try {
            DataOutputStream writer = new DataOutputStream(buf);
            writer.write(1);
            writer.writeUTF(event.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        event.getPlayer().sendPluginMessage(main, TAG, buf.toByteArray());
    }

    @EventHandler
    public void handle(UserSecureFetchedEvent event) {
        User user = userMap.get(event.getName());
        Player p = cast(user);
        if (user.getPassword().equals(event.getSecure())) {
            locked.remove(p.getUniqueId());
            p.sendMessage(getBungeeMessage());
        }
    }

    public void setCastInterval(int castInterval) {
        this.castInterval = castInterval;
    }

    public String[] getBungeeMessage() {
        if (bungeeSessionMsg == null) {
            bungeeSessionMsg = main.getConfig()
                    .getStringList("broadcast.bungeeSession")
                    .toArray(new String[0]);
        }
        return bungeeSessionMsg;
    }

    public void bind() {
        setContents(main.getConfig().getStringList("broadcast.content"));
        getMain().getServer()
                .getPluginManager()
                .registerEvents(this, main);
        setCastInterval(main.getConfig().getInt("broadcast.interval"));
    }

    private Player cast(User user) {
        return main.getServer().getPlayerExact(user.getUsername());
    }

    private Main getMain() {
        return main;
    }

    private Map<String, User> getUserMap() {
        return userMap;
    }

    private BukkitScheduler getScheduler() {
        return getMain().getServer().getScheduler();
    }

    private void register(Player player, ArrayVector<String> vector) {
        if (vector.remain() == 2) {
            register(player, vector.next(), vector.next());
        } else {
            send(player, "register.format", ChatColor.DARK_RED + "输入/register <密码> <重复密码>以完成注册");
        }
    }

    private void register(Player p, String pass, String next) {
        User user = getUserMap().get(p.getName());
        if (user == null) {
            send(p, "login.wait", ChatColor.DARK_RED + "用户账户数据拉取中，请稍候");
        } else if (user.valid()) {
            send(p, "register.failure", ChatColor.DARK_RED + "注册失败");
        } else if (pass.length() < 6) {
            send(p, "register.password.short", ChatColor.DARK_RED + "注册失败，请使用6位长度以上的密码");
        } else if (!pass.equals(next)) {
            send(p, "register.password.equal", ChatColor.DARK_RED + "注册失败，两次输入的密码内容不一致");
        } else {
            init(p, pass, user);
        }
    }

    private void init(Player p, String pass, User user) {
        init(user, pass, p);
        sendSecure(main, p, user.getPassword());
        if (main.isLogEvent()) main.execute(() -> {
            db.save(of(p, AppAccountEvent.REG_SUCCESS));
            db.save(of(p, LOG_SUCCESS));
        }, true);
        send(p, "register.succeed", ChatColor.GREEN + "注册成功");
    }

    private void login(Player player, ArrayVector<String> vector) {
        if (vector.remain() != 0) {
            User user = getUserMap().get(player.getName());
            if (user != null && user.valid() && user.valid(vector.next())) {
                locked.remove(player.getUniqueId());
                send(player, "login.done", ChatColor.GREEN + "登陆成功");
                if (main.isLogEvent()) main.execute(() -> {
                    db.save(of(player, LOG_SUCCESS));
                }, true);
                postEvent(player);
                sendSecure(main, player, user.getPassword());
            } else {
                send(player, "login.password", ChatColor.DARK_RED + "密码错误");
            }
        }
    }

    private void init(User user, String secure, Player p) {
        SecureUtil util = SecureUtil.DEFAULT;
        String salt = util.random(3);
        try {
            user.setPassword(util.digest(util.digest(secure) + salt));
        } catch (Exception e) {
            getMain().getLogger().warning(e.toString());
        }
        user.setSalt(salt);
        user.setUsername(p.getName());
        user.setRegip(p.getAddress().getAddress().getHostAddress());
        user.setRegdate(nowSec());
        user.setSecques("");
        main.execute(() -> {
            db.save(user);
        }, true);
        locked.remove(p.getUniqueId());
    }

    private int nowSec() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    private boolean isLocked(UUID uuid) {
        return locked.isLocked(uuid);
    }

    private void setContents(List<String> list) {
        contents = list.toArray(new String[list.size()]);
    }

}
