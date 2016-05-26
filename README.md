# Account
关联DZ论坛的登录插件。

## Require
下面列出来的是使用该插件的一些前置条件，不满足将无法使用该插件。
- Java 8
- SimpleORM v0.1.6(or above)
    - 这是我自己写的一个小东西，很小，在这里下：
    - https://github.com/caoli5288/SimpleORM/releases

## Command
下面列出的是该插件支持的指令。
- /login <密码>
    - 这个大家都懂，就是登录嘛。不登录的话什么都做不了。
    - 除了可以看30秒风景。
- /l
    - 就是/login的缩写而已。
- /register <密码> <密码>
    - 注册，需要重复两次密码。
    - 数据写入`pre_ucenter_members`表。
- /reg
    - 就是/register的缩写。
- /r
    - 就是/register的缩写的缩写。

## Permission
- account.deny.bypass
    - 允许拥有该权限的玩家进入新人阻止模式服务器。
- account.deny.prefix.bypass
    - 允许拥有该权限的玩家进入前缀阻止模式服务器。

有些同鞋可能会问阿，怎么不支持改密阿，怎么怎么不支持找回阿。
图样，说了关联dz论坛阿。

## Extra
插件启动后新建表`app_account_event`用以记录玩家登陆事件。

### Table Structure
Column | Type     | Description
-------|----------|--------------
id     | int      | Primary Key
name   | varchar  | Player's name
ip     | varchar  | Player's ip
type   | int      | Event Type
time   | datetime | Event Time

### Event Type
Id | Description
---|-----------------
0  | Register Succeed
1  | Register Failed
2  | Login Succeed
3  | Login Failed

## Bungee Session
支持蹦极服务器的会话保存。只要蹦极会话不断开就不必重复输入密码。

## Session
支持以会话的形式管理用户登陆。会话应该在玩家登陆之前请求服务器取得，
在玩家登陆之后以PluginChannel形式交服务器校验。(Unstable API)
![](1.png)

## License
本插件源代码及其二进制文件以GPLv2发布，请使用者遵守该协议。
