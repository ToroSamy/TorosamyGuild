package net.torosamy.torosamyGuild.commands


import net.torosamy.torosamyCore.manager.ConfigManager
import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.manager.GuildManager
import net.torosamy.torosamyGuild.pojo.Guild
import net.torosamy.torosamyGuild.type.Color
import net.torosamy.torosamyGuild.utils.ConfigUtil
import org.bukkit.Statistic
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class OwnerCommands {
    @Command("guild create <prefix>", requiredSender = Player::class)
    @Permission("torosamyguild.create")
    @CommandDescription("create guild")
    fun createGuild(sender: CommandSender, @Argument("prefix") prefix: String) {
        val player = sender as Player
        val alreadyGuild = GuildManager.getGuildByPlayer(player.name)
        if (alreadyGuild != null) {
            player.sendMessage(
                MessageUtil.text(ConfigUtil.langConfig.alreadyHasGuild.replace("{prefix}", alreadyGuild.prefix))
            )
            return
        }
        if (prefix.length < ConfigUtil.mainConfig.prefixMinLength) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.prefixTooShort.replace("{prefix}", prefix)))
            return
        }
        if (prefix.length > ConfigUtil.mainConfig.prefixMaxLength) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.prefixTooLong.replace("{prefix}", prefix)))
            return
        }


        val second = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20
        val condition = ConfigUtil.mainConfig.createTimeCondition  * 60 * 60
        if(second < condition) {
            player.sendMessage(MessageUtil.text(
                ConfigUtil.langConfig.createTimeCondition)
                .replace("{time}", ConfigUtil.mainConfig.createTimeCondition.toString())
            )
            return
        }

        val defaultColor = Color.valueOf(ConfigUtil.mainConfig.defaultColor)
        //内存区添加公会
        val guild = Guild.createGuild(player, prefix, defaultColor)

        GuildManager.addGuild(guild)
        //IO公会保存
        val config = Guild.getConfigByGuild(guild)
        ConfigManager.loadYaml(TorosamyGuild.plugin, "Guilds", "${guild.uuid}.yml",config)
        //发送消息
        player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.createSuccessful.replace("{prefix}", prefix)))
    }

    @Command("guild delete", requiredSender = Player::class)
    @Permission("torosamyguild.delete")
    @CommandDescription("delete guild")
    fun deleteGuild(sender: CommandSender) {
        val guild = GuildManager.isOwner(sender as Player)
        if (guild == null) {
            sender.sendMessage(MessageUtil.text(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }
        val time: Long = (System.currentTimeMillis() - guild.createTime) / 60000
        val duration: Long = ConfigUtil.mainConfig.deleteGuildCooldown - time
        if (duration > 0) {
            sender.sendMessage(MessageUtil.text(ConfigUtil.langConfig.deleteCooldown.replace("{duration}",duration.toString())))
            return
        }
        sender.sendMessage(MessageUtil.text(ConfigUtil.langConfig.deleteSuccessful).replace("{prefix}",guild.prefix))
        guild.enabled = false
        guild.saveConfig()
        GuildManager.deleteGuild(guild)
    }

    @Command("guild accept <player>", requiredSender = Player::class)
    @Permission("torosamyguild.accept")
    @CommandDescription("accept player`s apply")
    fun acceptApply(sender: CommandSender, @Argument("player") applyPlayer: String) {
        val player = sender as Player
        val guild = GuildManager.isOwner(player)
        if (guild == null) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }

        if (!guild.applyPlayers.contains(applyPlayer)) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.notFoundApply))
            return
        }

        GuildManager.deleteApplyByPlayer(applyPlayer)
        guild.playerList[applyPlayer] = 0.0
        player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.acceptApply.replace("{player}",applyPlayer)))
    }

    @Command("guild deny <player>", requiredSender = Player::class)
    @Permission("torosamyguild.deny")
    @CommandDescription("deny player`s apply")
    fun denyApply(sender: CommandSender, @Argument("player") applyPlayer: String) {
        val player = sender as Player
        val guild = GuildManager.isOwner(player)
        if (guild == null) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }

        if (!guild.applyPlayers.contains(applyPlayer)) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.notFoundApply))
            return
        }

        guild.applyPlayers.remove(applyPlayer)
        player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.denyApply.replace("{player}",applyPlayer)))
    }

    @Command("guild give <player>", requiredSender = Player::class)
    @Permission("torosamyguild.give")
    @CommandDescription("give guild to member")
    fun giveGuild(sender: CommandSender, @Argument("player") player: String) {
        val owner = sender as Player
        val guild = GuildManager.isOwner(owner)
        if (guild == null) {
            owner.sendMessage(MessageUtil.text(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }

        if (!guild.playerList.contains(player)) {
            owner.sendMessage(MessageUtil.text(ConfigUtil.langConfig.isNotGuildMember.replace("{player}", player)))
            return
        }
        //TODO 转交领地管理权限
        guild.playerList.remove(player)
        guild.playerList[owner.name] = 0.0
        guild.owner = player
        owner.sendMessage(MessageUtil.text(ConfigUtil.langConfig.giveGuildSuccessful))
    }

    @Command("guild rename <prefix>", requiredSender = Player::class)
    @Permission("torosamyguild.rename")
    @CommandDescription("rename guild`s prefix")
    fun renameGuild(sender: CommandSender, @Argument("prefix") prefix: String) {
        val player = sender as Player
        val guild = GuildManager.isOwner(player)
        if (guild == null) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }

        if (prefix.length < ConfigUtil.mainConfig.prefixMinLength) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.prefixTooShort.replace("{prefix}", prefix)))
            return
        }
        if (prefix.length > ConfigUtil.mainConfig.prefixMaxLength) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.prefixTooLong.replace("{prefix}", prefix)))
            return
        }


        player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.renameSuccessful
            .replace("{prefix}", guild.prefix)
            .replace("{new_prefix}",prefix)
        ))
        guild.prefix = prefix
    }

    @Command("guild color <color>", requiredSender = Player::class)
    @Permission("torosamyguild.color")
    @CommandDescription("change guild`s color")
    fun changeGuildColor(sender: CommandSender, @Argument("color") colorCode: String) {
        val player = sender as Player
        val guild = GuildManager.isOwner(player)
        if (guild == null) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }

        if (colorCode !in enumValues<Color>().map { it.name }) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.colorNotFound))
            return
        }
        val color = Color.valueOf(colorCode)
        guild.color = color


        player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.changeColorSuccessful.replace("{prefix}", guild.prefix)))
    }


    @Command("guild kick <player>", requiredSender = Player::class)
    @Permission("torosamyguild.kick")
    @CommandDescription("kick a player")
    fun kickPlayer(sender: CommandSender, @Argument("player") player: String) {
        val owner = sender as Player
        val guild = GuildManager.isOwner(owner)
        if (guild == null) {
            owner.sendMessage(MessageUtil.text(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }

        if (!guild.playerList.contains(player)) {
            owner.sendMessage(MessageUtil.text(ConfigUtil.langConfig.isNotGuildMember.replace("{player}", player)))
            return
        }

        guild.playerList.remove(player)
        owner.sendMessage(MessageUtil.text(ConfigUtil.langConfig.kickSuccessful.replace("{player}", player)))
    }


    @Command("guild check", requiredSender = Player::class)
    @Permission("torosamyguild.check")
    @CommandDescription("check apply")
    fun checkApply(sender: CommandSender) {
        val owner = sender as Player
        val guild = GuildManager.isOwner(owner)
        if (guild == null) {
            owner.sendMessage(MessageUtil.text(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }

        if (guild.applyPlayers.size == 0) {
            owner.sendMessage(MessageUtil.text(ConfigUtil.langConfig.noApplyToShow))
            return
        }
        sender.sendMessage(MessageUtil.text(ConfigUtil.langConfig.applyList))
        guild.applyPlayers.forEach { player -> owner.sendMessage(" - $player")}
    }
}