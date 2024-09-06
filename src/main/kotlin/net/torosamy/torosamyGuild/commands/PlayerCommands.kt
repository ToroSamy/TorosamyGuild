package net.torosamy.torosamyGuild.commands


import me.clip.placeholderapi.PlaceholderAPI
import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.manager.GuildManager
import net.torosamy.torosamyGuild.pojo.Guild
import net.torosamy.torosamyGuild.utils.ConfigUtil
import net.torosamy.torosamyGuild.utils.HoverUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.codehaus.plexus.util.StringUtils.isNumeric
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
        player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.applySuccessful.replace("{prefix}", guild.prefix)))
    }


    @Command("guild quit", requiredSender = Player::class)
    @Permission("torosamyguild.quit")
    @CommandDescription("quit guild")
    fun quitGuild(sender: CommandSender) {
        val player = sender as Player
        val guild = GuildManager.getGuildByPlayer(player.name)
        if (guild == null) {
            player.sendMessage(
                MessageUtil.text(
                    PlaceholderAPI.setPlaceholders(
                        player,
                        ConfigUtil.langConfig.notFoundGuild
                    )
                )
            )
            return
        }

        if (guild.owner == player.name) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.isGuildOwner.replace("{prefix}", guild.prefix)))
            return
        }

        guild.playerList.remove(player.name)
    }


    @Command("guild help")
    @Permission("torosamyguild.help")
    @CommandDescription("show guild command help")
    fun help(sender: CommandSender) {
        ConfigUtil.langConfig.commandHelp.forEach { sender.sendMessage(MessageUtil.text(it)) }
    }

    @Command("guild donate <amount>", requiredSender = Player::class)
    @Permission("torosamyguild.donate")
    @CommandDescription("donate score to guild")
    fun donateScoreDouble(sender: CommandSender, @Argument("amount") amount: String) {
        if(amount.toDoubleOrNull() == null) return
        val player = sender as Player
        val guild = GuildManager.getGuildByPlayer(player.name)
        if (guild == null) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.notFoundGuild))
            return
        }

        if (TorosamyGuild.economy.getBalance(player) < amount.toDouble()) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.scoreNoEnough))
            return
        }

        guild.score += amount.toDouble()
        guild.playerList[player.name] = guild.playerList[player.name]!! + amount.toDouble()
        TorosamyGuild.economy.withdrawPlayer(player, amount.toDouble())
        player.sendMessage(
            MessageUtil.text(
                ConfigUtil.langConfig.donateSuccessful
                    .replace("{score}", amount.toString())
                    .replace("{prefix}", guild.prefix)
            )
        )
    }


    @Command("guild info <page>", requiredSender = Player::class)
    @Permission("torosamyguild.info")
    @CommandDescription("show guild info")
    fun showGuildInfo(sender: CommandSender, @Argument("page") page: Int) {
        val player = sender as Player

        //如果现在的时间 和 上次排序的时间 的间距 大于排序冷却
        //则重新进行一次排序
        if (((System.currentTimeMillis() / 1000) - GuildManager.guildRank.first) > ConfigUtil.mainConfig.sortRankCooldown) GuildManager.sortGuilds()
        val rank: List<Guild> = GuildManager.guildRank.second
        var truePage = 1
        if(page > 1) truePage = page
        val startIndex = (truePage - 1) * ConfigUtil.mainConfig.maxPageShow
        val endIndex = (startIndex + ConfigUtil.mainConfig.maxPageShow).coerceAtMost(rank.size) // 保证endIndex不超过列表大小
        // 使用左闭右开的范围
        for (index in startIndex until endIndex) {
            val hover = HoverUtil.createCommandHover(
                MessageUtil.text(rank[index].color.color + rank[index].prefix),
                "/guild open ${rank[index].prefix}",
                MessageUtil.text(ConfigUtil.langConfig.clickOpenMenu)
            )
            HoverUtil.sendCommandHover(player,hover)
        }
    }


    @Command("guild open <prefix>", requiredSender = Player::class)
    @Permission("torosamyguild.open")
    @CommandDescription("show guild info")
    fun openGuildGUI(sender: CommandSender, @Argument("prefix") prefix: String) {
        val guild = GuildManager.getGuildByPrefix(prefix)
        if (guild == null) {
            sender.sendMessage(MessageUtil.text(ConfigUtil.langConfig.notFoundGuild))
            return
        }

        val player = sender as Player
        guild.guildGUI.openGui(player)
    }


    @Command("guild apply", requiredSender = Player::class)
    @Permission("torosamyguild.apply")
    @CommandDescription("show all apply about self")
    fun showAllApply(sender: CommandSender) {
        val player = sender as Player

        val guild = GuildManager.getGuildByPlayer(player.name)
        if (guild != null) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.alreadyHasGuild))
            for (value in GuildManager.guilds.values) {
                value.applyPlayers.remove(player.name)
            }
            return
        }


        sender.sendMessage(MessageUtil.text(ConfigUtil.langConfig.applyList))
        for (guild in GuildManager.guilds.values) {
            if (guild.applyPlayers.contains(sender.name)) {
                sender.sendMessage(MessageUtil.text("${guild.color.color}${guild.prefix}"))
            }
        }
    }

    @Command("guild cancel <prefix>", requiredSender = Player::class)
    @Permission("torosamyguild.cancel")
    @CommandDescription("cancel join the guild")
    fun cancelApply(sender: CommandSender, @Argument("prefix") prefix: String) {
        val player = sender as Player

        val guild = GuildManager.getGuildByPlayer(player.name)
        if (guild != null) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.alreadyHasGuild))
            for (value in GuildManager.guilds.values) {
                value.applyPlayers.remove(player.name)
            }
            return
        }

        val guildByPrefix = GuildManager.getGuildByPrefix(prefix)
        if (guildByPrefix == null) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.notFoundGuild))
            return
        }

        guildByPrefix.applyPlayers.remove(player.name)
        player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.cancelApplySuccessful.replace("{prefix}", prefix)))
    }

    @Command("test", requiredSender = Player::class)
    @Permission("torosamyguild.test")
    @CommandDescription("donate score to guild")
    fun test(sender: CommandSender) {
        val guild = GuildManager.getGuildByPrefix("云间幽谷")
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
        println(guild.score)
        println(guild.getLevel())

    }
}