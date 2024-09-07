## Dependency
  - TorosamyCore
  - PlaceholderAPI
  - Residence
  - Vault
## Usage
1. download [TorosamyCore](https://github.com/ToroSamy/TorosamyCore) as a dependency plugin
2. put the **dependencies** and this plugin into your plugins folder and start your server
3. Modify the default configuration file generated and restart your server
## Permission
- - **Usage:** /guild create guild-name
  - **Description:** create guild
  - **Permission:** torosamyguild.create
  <br>
- - **Usage:** /guild delete
  - **Description:** Dissolve the guild
  - **Permission:** torosamyguild.delete
  <br>
- - **Usage:** /guild deny|accept player
  - **Description:** deny|accept apply
  - **Permission:** torosamyguild.deny|torosamyguild.accept
  <br>
- - **Usage:** /guild give member
  - **Description:** Give the position of transfer manager to someone else
  - **Permission:** torosamyguild.give
  <br>
- - **Usage:** /guild rename new-guild-name
  - **Description:** change guild`s prefix
  - **Permission:** torosamyitem.rename
  <br>
- - **Usage:** /guild color color
  - **Description:** change guild`s color
  - **Permission:** torosamyitem.color
  <br>
- - **Usage:** /guild kick member
  - **Description:** kick a member
  - **Permission:** torosamyitem.kick
  <br>
- - **Usage:** /guild check
  - **Description:** View all applications from the guild
  - **Permission:** torosamyitem.check
  <br>
- - **Usage:** /guild join guild-name
  - **Description:** Apply to join the guild
  - **Permission:** torosamyitem.join
  <br>
- - **Usage:** /guild quit
  - **Description:** quit guild
  - **Permission:** torosamyitem.quit
  <br>
- - **Usage:** /guild help
  - **Description:** Display help for all instructions
  - **Permission:** torosamyitem.help
  <br>
- - **Usage:** /guild donate amount
  - **Description:** Donate a specified amount of gold coins to the guild
  - **Permission:** torosamyitem.donate
  <br>
- - **Usage:** /guild info page
  - **Description:** Display the guild with the highest page level specified
  - **Permission:** torosamyitem.info
  <br>
- - **Usage:** /guild open prefix
  - **Description:** open guild`s GUI
  - **Permission:** torosamyitem.open
  <br>
- - **Usage:** /guild apply
  - **Description:** Display all the applications that have been sent out by oneself
  - **Permission:** torosamyitem.apply
  <br>
- - **Usage:** /guild cancel prefix
  - **Description:** cancel apply
  - **Permission:** torosamyitem.cancel
  <br>
- - **Usage:** /gres create res-name
  - **Description:** create guild`s residence
  - **Permission:** torosamyguild.res.create
  <br>
- - **Usage:** /gres expand distance
  - **Description:** expand guild`s residence
  - **Permission:** torosamyguild.res.expand
## Config
### config.yml
```yml
default-color: GOLD
prefix-max-length: 5
prefix-min-length: 2

#单位分钟
delete-guild-cooldown: 21600

#玩家在线多少小时才能创建公会
create-time-condition: 72

#一页展示多少个公会
max-page-show: 5

#展示公会时 多久才排一次序 单位s
sort-rank-cooldown: 600
#公会圈地价格
res-cost-block: 0.01

# 若不开启 只会在stop服务器时保存
# 定时保存 防止多次读写文件
auto-save:
  enabled: true
  # 多久保存一次
  minutes: 30

# 基本信息展示的材质
gui-basic-info-item: NETHER_STAR
# 领地传送的材质
#gui-visit-item: BEACON
# 成员捐赠的材质
member-donation-item: BEACON
default-guild-config:
  score: 0.0
  enabled: true
  # 创建日期 用于设置多久可以解散
  create-time: ""
  uuid: ""
  # 公会名
  prefix: ""
  # 会长名
  owner: ""
  # 领地名
  res: ""
  # 公会成员
  player-list: []
  color: GOLD
  apply-players: []
database:
  enabled: false
  host: "localhost"
  port: "3306"
  database: "torosamy_guild"
  username: "root"
  password: "root"
```


### lang.yml
```yml
default-prefix: "&7&l暂无公会"
reload-message: "&b[服务器娘]&a插件 &eTorosamyGuild &a重载成功喵~"
already-has-guild: "&b[服务器娘]&c您目前隶属于公会 &e{prefix} &c无法进行此操作"
prefix-too-long: "&b[服务器娘]&c公会名 &e{prefix} &c过长"
prefix-too-short: "&b[服务器娘]&c公会名 &e{prefix} &c过短"
create-successful: "&b[服务器娘]&a公会 &e{prefix} &a创建成功!"
is-not-guild-owner: "&b[服务器娘]&a您不是任何一个公会的会长"
delete-cooldown: "&b[服务器娘]&c自公会创建后还需 &e{duration} &c分钟才能进行此操作"
delete-successful: "&b[服务器娘]&a公会 &e{prefix} &a被您成功解散"
not-found-guild: "&b[服务器娘]&c尚未查询到相应的公会"
apply-repeat: "&b[服务器娘]&c请勿重复申请加入公会"
apply-successful: "&b[服务器娘]&a成功向公会 &e{prefix} &a提交加入申请"
not-found-apply: "&b[服务器娘]&c尚未查询到相应的申请"
accept-apply: "&b[服务器娘]&a已同意玩家 &e{player} &a加入公会"
deny-apply: "&b[服务器娘]&c已拒绝玩家 &e{player} &c加入公会"
is-guild-owner: "&b[服务器娘]&c您目前负责公会 &e{prefix} &c无法进行此操作"
is-not-guild-member: "&b[服务器娘]&c玩家 &e{player} &c必须是公会成员"
give-guild-successful: "&b[服务器娘]&a公会转让成功"
rename-successful: "&b[服务器娘]&a公会 &e{prefix} &a成功更名为 &e{new_prefix}"
change-color-successful: "&b[服务器娘]&a公会 &e{prefix} &a成功更换颜色"
command-help:
  - "&8&m---------------------&6公会指令&8&m---------------------"
  - "&6/guild create <guild-name>&7 - 创建公会"
  - "&6/guild delete&7 - 解散公会"
  - "&6/guild deny <player>&7 - 拒绝入会申请"
  - "&6/guild accept <player>&7 - 同意入会申请"
  - "&6/guild give <member>&7 - 转让公会"
  - "&6/guild rename <new-guild-name>&7 - 更改公会名称"
  - "&6/guild color <color>&7 - 更改公会颜色"
  - "&6/guild kick <member>&7 - 移出玩家"
  - "&6/guild check&7 - 查看申请列表"
  - "&6/guild join <guild-name>&7 - 申请加入公会"
  - "&6/guild quit&7 - 退出公会"
  - "&6/guild help&7 - 指令提示"
  - "&6/guild donate <amount>&7 - 捐赠资金"
  - "&6/guild info <page>&7 - 展示所有公会"
  - "&6/guild open <prefix>&7 - 公会展示"
  - "&6/guild apply&7 - 查看自己的申请"
  - "&6/guild cancel <prefix>&7 - 取消申请"
  - "&6/gres create <res-name>&7 - 创建公会领地"
  - "&6/gres expand <distance>&7 - 拓展公会领地"
  - "&8&m-----------------------------------------------------"
color-not-found: "&b[服务器娘]&c未查找到相应的颜色"
create-time-condition: "&b[服务器娘]&c您必须在线满 &e{time} &c小时 才可创建公会"
kick-successful: "&b[服务器娘]&a成功将玩家 &e{player} &a移出公会"
no-apply-to-show: "&b[服务器娘]&a尚未查询到任何加入申请"
apply-list: "&e&m----&6申请列表&e&m----"
score-no-enough: "&b[服务器娘]&c您的剩余积分不足以进行此操作"
donate-successful: "&b[服务器娘]&a成功向公会 &e{prefix} &a捐赠 &e{score} &a积分"
click-open-menu: "&a点击打开公会面板"
cancel-apply-successful: "&b[服务器娘]&a成功取消向公会 &e{prefix} &a发起的加入申请"
guild-gui-title: "公会信息"
guild-basic-info:
  - '&6&m----------------'
  - '&6 ▪ &6&l会名&f:{prefix}'
  - '&6 ▪ &6&l会长&f:&e{owner}'
  - '&6 ▪ &6&l等级&f:&e{level}'
  - '&6 ▪ &6&l积分&f:&e{score}'
  - '&6 ▪ &6&l领地&f:&e{res}'
  - '&6&m----------------'
  - '&6&l[Click]&e&l左键参观'
  - '&6&m----------------'
basic-info-display: "&6基本信息"
#guild-visit: "&6点击参观"
member-donation: "&6成员捐赠"
not-found-res: "&b[服务器娘]&c尚未查找到相应的领地"
already-has-res: "&b[服务器娘]&c请勿重复创建公会领地"
create-res-successful: "&b[服务器娘]&a公会领地创建成功"
guild-score-no-enough: "&b[服务器娘]&c公会的剩余积分不足以进行此操作"
expand-res-successful: "&b[服务器娘]&a成功拓展公会领地"
```


## Contact Author
- qq: 1364596766
- website: https://www.torosamy.net

## License

[GPL-3.0 license](./LICENSE)
