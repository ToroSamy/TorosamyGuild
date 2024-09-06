package net.torosamy.torosamyGuild.commands

import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.manager.GuildManager
import net.torosamy.torosamyGuild.utils.ConfigUtil
import net.torosamy.torosamyGuild.utils.PapiUtil
import net.torosamy.torosamyGuild.utils.SchedulerUtil
import org.bukkit.command.CommandSender
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class AdminCommands {
    @Command("guild reload")
    @Permission("torosamyguild.admin")
    @CommandDescription("重载TorosamyGuild配置文件")
    fun reloadConfig(sender: CommandSender) {
        ConfigUtil.reloadConfig()
        PapiUtil.reload()
        GuildManager.loadGuilds()
        SchedulerUtil.registerScheduler()
        sender.sendMessage(MessageUtil.text(ConfigUtil.langConfig.reloadMessage))
    }


    @Command("guild delete <prefix>")
    @Permission("torosamyguild.admin")
    @CommandDescription("delete guild")
    fun deleteGuild(sender: CommandSender, @Argument("prefix") prefix: String) {
        val guild = GuildManager.getGuildByPrefix(prefix)
        if (guild == null) {
            sender.sendMessage(MessageUtil.text(ConfigUtil.langConfig.notFoundGuild))
            return
        }

        sender.sendMessage(MessageUtil.text(ConfigUtil.langConfig.deleteSuccessful).replace("{prefix}",guild.prefix))
        guild.enabled = false
        guild.saveConfig()
        GuildManager.deleteGuild(guild)
    }


    @Command("guild save")
    @Permission("torosamyguild.admin")
    @CommandDescription("save data")
    fun saveData(sender: CommandSender) {
        GuildManager.saveGuilds()
    }



}