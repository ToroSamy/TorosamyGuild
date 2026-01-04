package net.torosamy.torosamyGuild.commands



import com.bekvon.bukkit.residence.Residence
import com.bekvon.bukkit.residence.api.ResidenceApi
import com.bekvon.bukkit.residence.commands.give
import net.torosamy.torosamyCore.config.Config
import net.torosamy.torosamyCore.config.ConfigFile
import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.api.TorosamyGuildAPI
import net.torosamy.torosamyGuild.pojo.Guild
import net.torosamy.torosamyGuild.type.Color
import net.torosamy.torosamyGuild.utils.ConfigUtil
import org.bukkit.Bukkit
import org.bukkit.Statistic
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class OwnerCommands {
    @Command("guild create <name>", requiredSender = Player::class)
    @Permission("torosamyguild.create")
    @CommandDescription("创建公会")
    fun createGuild(sender: CommandSender, @Argument("name") name: String) {
        val player = sender as Player
        
        val alreadyGuild = TorosamyGuildAPI.getGuild(player)
        
        if (alreadyGuild != null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.alreadyHasGuild.replace("{prefix}", alreadyGuild.getPrefix())))
            return
        }
        
        if (name.length < ConfigUtil.mainConfig.prefixMinLength) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.prefixTooShort.replace("{name}", name)))
            return
        }
        
        if (name.length > ConfigUtil.mainConfig.prefixMaxLength) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.prefixTooLong.replace("{name}", name)))
            return
        }


        val second = player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20
        val condition = ConfigUtil.mainConfig.createTimeCondition  * 60 * 60
        
        if(second < condition) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.createTimeCondition).replace(
                "{time}", 
                ConfigUtil.mainConfig.createTimeCondition.toString()
            ))
            return
        }
        
        val guild = Guild(player, name)
        TorosamyGuildAPI.addGuild(guild)
        
        guild.generateConfig().save(
            ConfigFile(TorosamyGuild.plugin, "${guild.uuid}.yml", listOf("Guilds")).getFile(false)
        )
        
        player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.createSuccessful.replace("{prefix}", guild.getPrefix())))
    }

    @Command("guild delete", requiredSender = Player::class)
    @Permission("torosamyguild.delete")
    @CommandDescription("解散公会")
    fun deleteGuild(sender: CommandSender) {
        val player = sender as Player

        val guild = TorosamyGuildAPI.getGuild(player)

        if (guild == null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundGuild))
            return
        }

        if (!guild.isOwner(player.name)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }

        val time: Long = (System.currentTimeMillis() - guild.createTime) / 60000
        val duration: Long = ConfigUtil.mainConfig.deleteGuildCooldown - time
        if (duration > 0) {
            sender.sendMessage(MessageUtil.format(ConfigUtil.langConfig.deleteCooldown.replace("{duration}",duration.toString())))
            return
        }
        sender.sendMessage(MessageUtil.format(ConfigUtil.langConfig.deleteSuccessful).replace("{prefix}",guild.getPrefix()))

        guild.enabled = false
        TorosamyGuildAPI.deleteGuild(guild)
    }

    @Command("guild accept <player>", requiredSender = Player::class)
    @Permission("torosamyguild.accept")
    @CommandDescription("同意玩家的加入申请")
    fun acceptApply(sender: CommandSender, @Argument("player") applyPlayer: String) {
        val player = sender as Player
        
        if (player.name == applyPlayer) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.interactionSelfError))
            return
        }
        
        val guild = TorosamyGuildAPI.getGuild(player)

        if (guild == null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundGuild))
            return
        }

        if (!guild.isOwner(player.name)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }
        

        if (!guild.applied(applyPlayer)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundApply))
            return
        }

        TorosamyGuildAPI.clearApply(applyPlayer)

        if (!guild.join(applyPlayer)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.alreadyHasGuild.replace("{prefix}",guild.getPrefix())))
            TorosamyGuildAPI.clearApply(player.name)

            return
        }
        
        player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.acceptApply.replace("{player}",applyPlayer)))

        val member = Bukkit.getPlayer(applyPlayer)
        if (member != null && member.isOnline) {
            member.sendMessage(MessageUtil.format(ConfigUtil.langConfig.acceptApply.replace("{player}",applyPlayer)))
        }
    }

    @Command("guild deny <player>", requiredSender = Player::class)
    @Permission("torosamyguild.deny")
    @CommandDescription("拒绝玩家的加入申请")
    fun denyApply(sender: CommandSender, @Argument("player") applyPlayer: String) {
        val player = sender as Player

        if (player.name == applyPlayer) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.interactionSelfError))
            return
        }
        
        val guild = TorosamyGuildAPI.getGuild(player)

        if (guild == null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundGuild))
            return
        }

        if (!guild.isOwner(player.name)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }


        if (!guild.applied(applyPlayer)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundApply))
            return
        }

        guild.removeApply(player.name)
        
        player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.denyApply.replace("{player}",applyPlayer)))

        val member = Bukkit.getPlayer(applyPlayer)
        if (member != null && member.isOnline) {
            member.sendMessage(MessageUtil.format(ConfigUtil.langConfig.denyApply.replace("{player}",applyPlayer)))
        }
    }

    @Command("guild give <player>")
    @Permission("torosamyguild.give")
    @CommandDescription("将公会转让给一名成员")
    fun giveGuild(player: Player, @Argument("player") memberName: String) {
        if (player.name == memberName) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.interactionSelfError))
            return
        }
        
        val guild = TorosamyGuildAPI.getGuild(player)

        if (guild == null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundGuild))
            return
        }

        if (!guild.isOwner(player.name)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }
        
        if (!guild.isMember(memberName)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.isNotGuildMember.replace("{player}", memberName)))
            return
        }


        if (!guild.give(player, memberName)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.giveResFail))
            return
        }
        
        player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.giveGuildSuccessful))
    }

    @Command("guild rename <name>", requiredSender = Player::class)
    @Permission("torosamyguild.rename")
    @CommandDescription("修改公会的名字")
    fun renameGuild(sender: CommandSender, @Argument("name") name: String) {
        val player = sender as Player
        
        val guild = TorosamyGuildAPI.getGuild(player)

        if (guild == null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundGuild))
            return
        }

        if (!guild.isOwner(player.name)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }

        if (name.length < ConfigUtil.mainConfig.prefixMinLength) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.prefixTooShort.replace("{name}", name)))
            return
        }
        if (name.length > ConfigUtil.mainConfig.prefixMaxLength) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.prefixTooLong.replace("{name}", name)))
            return
        }
        
        val oldPrefix = guild.getPrefix()
        
        guild.name = name
        val newPrefix = guild.getPrefix()
        
        player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.renameSuccessful
            .replace("{prefix}", oldPrefix)
            .replace("{new_prefix}", newPrefix)
        ))
    }

    @Command("guild color <color>", requiredSender = Player::class)
    @Permission("torosamyguild.color")
    @CommandDescription("修改公会头衔颜色")
    fun changeGuildColor(sender: CommandSender, @Argument("color") colorCode: String) {
        val player = sender as Player

        val guild = TorosamyGuildAPI.getGuild(player)

        if (guild == null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundGuild))
            return
        }

        if (!guild.isOwner(player.name)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }
        
        if (colorCode !in enumValues<Color>().map { it.name }) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.colorNotFound))
            return
        }
        
        val color = Color.valueOf(colorCode)
        guild.color = color
        
        player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.changeColorSuccessful.replace("{prefix}", guild.getPrefix())))
    }


    @Command("guild kick <player>", requiredSender = Player::class)
    @Permission("torosamyguild.kick")
    @CommandDescription("将玩家踢出公会")
    fun kickPlayer(sender: CommandSender, @Argument("player") memberName: String) {
        val player = sender as Player

        if (player.name == memberName) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.interactionSelfError))
            return
        }

        val guild = TorosamyGuildAPI.getGuild(player)

        if (guild == null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundGuild))
            return
        }

        if (!guild.isOwner(player.name)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }

        if (!guild.isMember(memberName)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.isNotGuildMember.replace("{player}", memberName)))
            return
        }

        guild.quit(memberName)
        player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.kickSuccessful.replace("{player}", memberName)))
    }


    @Command("guild check", requiredSender = Player::class)
    @Permission("torosamyguild.check")
    @CommandDescription("查看所有想要加入公会的玩家")
    fun checkApply(sender: CommandSender) {
        val player = sender as Player
        
        val guild = TorosamyGuildAPI.getGuild(player)

        if (guild == null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundGuild))
            return
        }

        if (!guild.isOwner(player.name)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }
        
        val applies = guild.lookApplies()
        
        if (applies.isEmpty()) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.noApplyToShow))
            return
        }

        player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.applyList))
        
        applies.forEach { 
            player.sendMessage(" - $it")
        }
    }
}