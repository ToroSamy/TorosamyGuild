package net.torosamy.torosamyGuild.commands


import me.clip.placeholderapi.PlaceholderAPI
import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.manager.GuildManager
import net.torosamy.torosamyGuild.utils.ConfigUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class PlayerCommands {


    @Command("guild join <prefix>", requiredSender = Player::class)
    @Permission("torosamyguild.join")
    @CommandDescription("join guild")
    fun applyJoinGuild(sender: CommandSender, @Argument("prefix") prefix: String) {
        val player = sender as Player
        if (GuildManager.getGuildByPlayer(player.name) != null) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.alreadyHasGuild.replace("{prefix}", prefix)))
            return
        }

        val guild = GuildManager.getGuildByPrefix(prefix)
        if (guild == null) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.notFoundGuild))
            return
        }

        if (guild.applyPlayers.contains(player.name)) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.applyRepeat))
            return
        }

        guild.applyPlayers.add(player.name)
        player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.applySuccessful.replace("{prefix}",guild.prefix)))
    }


    @Command("guild quit", requiredSender = Player::class)
    @Permission("torosamyguild.quit")
    @CommandDescription("quit guild")
    fun quitGuild(sender: CommandSender) {
        val player = sender as Player
        val guild = GuildManager.getGuildByPlayer(player.name)
        if (guild == null) {
            player.sendMessage(MessageUtil.text(PlaceholderAPI.setPlaceholders(player, ConfigUtil.langConfig.notFoundGuild)))
            return
        }

        if (guild.owner == player.name) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.isGuildOwner.replace("{prefix}",guild.prefix)))
            return
        }

        guild.playerList.remove(player.name)
    }

    @Command("guild info <prefix>")
    @Permission("torosamyguild.admin")
    @CommandDescription("show guild info")
    fun showGuildInfo(sender: CommandSender, @Argument("prefix") prefix: String) {
        val guild = GuildManager.getGuildByPrefix(prefix)
        if (guild == null) {
            sender.sendMessage(MessageUtil.text(ConfigUtil.langConfig.notFoundGuild))
            return
        }
        //TODO
        println(guild.owner)
        guild.playerList.forEach(::println)
        guild.applyPlayers.forEach(::println)
        println(guild.color)
        println(guild.prefix)
    }
    @Command("guild help")
    @Permission("torosamyguild.help")
    @CommandDescription("show guild command help")
    fun help(sender: CommandSender) {
        ConfigUtil.langConfig.commandHelp.forEach { sender.sendMessage(MessageUtil.text(it)) }
    }
}