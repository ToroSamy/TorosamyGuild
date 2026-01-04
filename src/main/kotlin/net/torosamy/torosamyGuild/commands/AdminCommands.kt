package net.torosamy.torosamyGuild.commands

import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.api.TorosamyGuildAPI
import net.torosamy.torosamyGuild.utils.ConfigUtil
import net.torosamy.torosamyGuild.utils.PapiUtil
import net.torosamy.torosamyGuild.utils.SchedulerUtil
import org.bukkit.command.CommandSender
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
        TorosamyGuildAPI.loadGuilds()
        SchedulerUtil.registerScheduler()
        sender.sendMessage(MessageUtil.format(ConfigUtil.langConfig.reloadMessage))
    }

    @Command("guild save")
    @Permission("torosamyguild.admin")
    @CommandDescription("手动保存公会数据")
    fun saveData(sender: CommandSender) {
        TorosamyGuildAPI.saveGuilds()
    }
}