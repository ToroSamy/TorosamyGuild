package net.torosamy.torosamyGuild.commands

import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.manager.GuildManager
import net.torosamy.torosamyGuild.utils.ConfigUtil
import net.torosamy.torosamyGuild.utils.PapiUtil
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class AdminCommands {
    @Command("guild reload")
    @Permission("torosamyguild.reload")
    @CommandDescription("重载TorosamyGuild配置文件")
    fun reloadConfig(sender: CommandSender) {
        ConfigUtil.reloadConfig()
        PapiUtil.reload()
        GuildManager.loadGuilds()
        sender.sendMessage(MessageUtil.text(ConfigUtil.langConfig.reloadMessage))
    }
}