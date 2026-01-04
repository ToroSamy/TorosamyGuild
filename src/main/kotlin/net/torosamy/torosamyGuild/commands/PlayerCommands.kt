package net.torosamy.torosamyGuild.commands


import net.milkbowl.vault.economy.EconomyResponse
import net.torosamy.torosamyCore.api.TorosamyCoreAPI
import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.api.TorosamyGuildAPI
import net.torosamy.torosamyGuild.pojo.Guild
import net.torosamy.torosamyGuild.utils.ConfigUtil
import net.torosamy.torosamyGuild.utils.HoverUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class PlayerCommands {
    @Command("guild join <name>", requiredSender = Player::class)
    @Permission("torosamyguild.join")
    @CommandDescription("申请加入公会")
    fun applyJoinGuild(sender: CommandSender, @Argument("name") name: String) {
        val player = sender as Player
        
        val playerGuild = TorosamyGuildAPI.getGuild(player)
        
        if (playerGuild != null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.alreadyHasGuild.replace("{prefix}", playerGuild.getPrefix())))
            return
        }

        val guild = TorosamyGuildAPI.getGuild(name)
        
        if (guild == null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundGuild))
            return
        }

        if (guild.applied(player.name)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.applyRepeat))
            return
        }

        guild.apply(player.name)
        player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.applySuccessful.replace("{prefix}", guild.getPrefix())))
    }


    @Command("guild quit", requiredSender = Player::class)
    @Permission("torosamyguild.quit")
    @CommandDescription("退出公会")
    fun quitGuild(sender: CommandSender) {
        val player = sender as Player
        
        val guild = TorosamyGuildAPI.getGuild(player)
        
        if (guild == null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundGuild))
            return
        }

        if (guild.isOwner(player.name)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.isGuildOwner.replace("{prefix}", guild.getPrefix())))
            return
        }

        guild.quit(player.name)
    }


    @Command("guild help")
    @Permission("torosamyguild.help")
    @CommandDescription("显示公会使用帮助")
    fun help(sender: CommandSender) {
        ConfigUtil.langConfig.commandHelp.forEach {
            sender.sendMessage(MessageUtil.format(it)) 
        }
    }

    @Command("guild donate <amount>", requiredSender = Player::class)
    @Permission("torosamyguild.donate")
    @CommandDescription("给自己的公会捐赠积分")
    fun donateScoreDouble(sender: CommandSender, @Argument("amount") amount: Double) {
        if(amount <= 0) {
            return
        }
        
        val player = sender as Player

        val guild = TorosamyGuildAPI.getGuild(player)
        
        if (guild == null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundGuild))
            return
        }

        if (TorosamyGuild.economy.getBalance(player) < amount) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.scoreNoEnough))
            return
        }
        val response = TorosamyGuild.economy.withdrawPlayer(player, amount)
        
        if (response.type == EconomyResponse.ResponseType.FAILURE) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.scoreNoEnough))
            return
        }

        guild.donate(player.name, amount)
        
        player.sendMessage(MessageUtil.format(
                ConfigUtil.langConfig.donateSuccessful
                    .replace("{score}", amount.toString())
                    .replace("{prefix}", guild.getPrefix())
        ))
    }

    @Command("guild open <name>", requiredSender = Player::class)
    @Permission("torosamyguild.open")
    @CommandDescription("打开公会展示菜单")
    fun openGuildGUI(sender: CommandSender, @Argument("name") name: String) {
        val guild = TorosamyGuildAPI.getGuild(name)
        
        if (guild == null) {
            sender.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundGuild))
            return
        }
        
        (sender as Player).openInventory(guild.generateGUI())
    }


    @Command("guild apply", requiredSender = Player::class)
    @Permission("torosamyguild.apply")
    @CommandDescription("显示自己所有的加入申请")
    fun showAllApply(sender: CommandSender) {
        val player = sender as Player

        val guild = TorosamyGuildAPI.getGuild(player)
        
        if (guild != null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.alreadyHasGuild.replace("{prefix}",guild.getPrefix())))
            TorosamyGuildAPI.clearApply(player.name)
            
            return
        }
        
        sender.sendMessage(MessageUtil.format(ConfigUtil.langConfig.applyList))
        
        val guilds = TorosamyGuildAPI.getGuildsApplied(player)
        
        for (it in guilds) {
            sender.sendMessage(MessageUtil.format(it.getPrefix()))
        }
    }

    @Command("guild cancel <name>", requiredSender = Player::class)
    @Permission("torosamyguild.cancel")
    @CommandDescription("取消加入公会")
    fun cancelApply(sender: CommandSender, @Argument("name") name: String) {
        val player = sender as Player

        val playerGuild = TorosamyGuildAPI.getGuild(player)

        if (playerGuild != null) {
            TorosamyGuildAPI.clearApply(player.name)
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.alreadyHasGuild.replace("{prefix}", playerGuild.getPrefix())))
            return
        }

        val guild = TorosamyGuildAPI.getGuild(name)

        if (guild == null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundGuild))
            return
        }

        if (!guild.applied(player.name)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundApply))
            return
        }

        if (!guild.removeApply(player.name)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundApply))
            return
        }

        player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.cancelApplySuccessful.replace("{prefix}", guild.getPrefix())))
    }
    
    @Command("guild rank", requiredSender = Player::class)
    @Permission("torosamyguild.rank")
    @CommandDescription("显示公会排行榜")
    fun defaultGuildRank(sender: CommandSender) {
        guildRank(sender, 1)
    }
    
    @Command("guild rank <page>", requiredSender = Player::class)
    @Permission("torosamyguild.rank")
    @CommandDescription("显示公会排行榜")
    fun guildRank(sender: CommandSender, @Argument("page") page: Int) {
        val player = sender as Player

        val guilds = TorosamyGuildAPI.updateRank()
        
        if (guilds.isEmpty()) {
            sender.sendMessage(MessageUtil.format(ConfigUtil.langConfig.topUpdating))
            return
        }
        val pageSize = ConfigUtil.mainConfig.maxPageShow
        
        val startIndex = TorosamyCoreAPI.getStartIndex(guilds.size, pageSize, page)

        val endIndex = TorosamyCoreAPI.getEndIndex(guilds.size, pageSize, page)

        val totalPage = TorosamyCoreAPI.getTotalPage(guilds.size, pageSize)

        if (page < 1 || page > totalPage || startIndex == -1 || endIndex == -1) {
            sender.sendMessage(MessageUtil.format(ConfigUtil.langConfig.pageError))
            return
        }

        val nextPage = if (page + 1 > totalPage) totalPage else page + 1

        ConfigUtil.langConfig.topHeader.forEach { 
            sender.sendMessage(MessageUtil.format(it
                    .replace("%update_time%", MessageUtil.formatTimestamp(TorosamyGuildAPI.getLastUpdateRankTimestamp()))
                    .replace("%now_page%", page.toString())
                    .replace("%total_page%", totalPage.toString())
                    .replace("%next_page%", nextPage.toString())
                )
            )
        }

        for (i in startIndex until endIndex) {
            if (i >= guilds.size) {
                break
            }

            val guild: Guild = guilds[i]
            
            val textComponent = HoverUtil.createCommandHover(
                MessageUtil.format((i + 1).toString() + ". " + guild.getPrefix()),
                "/guild open " + guild.name,
                MessageUtil.format(ConfigUtil.langConfig.clickOpenMenu)
            )

            HoverUtil.sendCommandHover(player, textComponent)
        }

        ConfigUtil.langConfig.topFooter.forEach {
            sender.sendMessage(MessageUtil.format(it
                    .replace("%update_time%", MessageUtil.formatTimestamp(TorosamyGuildAPI.getLastUpdateRankTimestamp()))
                    .replace("%now_page%", page.toString())
                    .replace("%total_page%", totalPage.toString())
                    .replace("%next_page%", nextPage.toString())
                )
            )
        }
        
        
    }
}