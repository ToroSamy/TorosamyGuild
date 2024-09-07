package net.torosamy.torosamyGuild.commands

import com.bekvon.bukkit.residence.Residence
import com.bekvon.bukkit.residence.api.ResidenceApi
import com.bekvon.bukkit.residence.protection.ClaimedResidence
import com.bekvon.bukkit.residence.protection.CuboidArea
import com.bekvon.bukkit.residence.selection.SelectionManager.Direction
import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.manager.GuildManager
import net.torosamy.torosamyGuild.utils.ConfigUtil
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class ResCommands {
    @Command("gres create <name>", requiredSender = Player::class)
    @Permission("torosamyguild.res.create")
    @CommandDescription("create guild res")
    fun createRes(sender: CommandSender, @Argument("name") name: String)  {
        val player = sender as Player
        val guild = GuildManager.isOwner(player)
        if (guild == null) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }

        if (guild.residence.isNotEmpty()) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.alreadyHasRes))
            return
        }
        val cuboidArea = Residence.getInstance().selectionManager.getSelectionCuboid(player)
        val cost = cuboidArea.size * ConfigUtil.mainConfig.resCostBlock

        if (guild.score < cost) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.guildScoreNoEnough))
            return
        }


        if(!player.isOp) {
            player.isOp = true
            Bukkit.dispatchCommand(player,"resadmin create ${name}")
            Bukkit.dispatchCommand(player,"resadmin server ${name}")
            player.isOp = false
        }else {
            Bukkit.dispatchCommand(player,"resadmin create ${name}")
            Bukkit.dispatchCommand(player,"resadmin server ${name}")
        }
        Bukkit.dispatchCommand(TorosamyGuild.plugin.server.consoleSender,"resadmin pset ${name} ${player.name} admin true")
        guild.score -= cost
        guild.residence = name
        player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.createResSuccessful))
    }

    @Command("gres expand <distance>", requiredSender = Player::class)
    @Permission("torosamyguild.res.expand")
    @CommandDescription("expand guild res")
    fun expandRes(sender: CommandSender, @Argument("distance") distance: String)  {
        val player = sender as Player
        val guild = GuildManager.isOwner(player)
        if (guild == null) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }

        if (guild.residence.isEmpty()) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.notFoundRes))
            return
        }

        val residence: ClaimedResidence? = ResidenceApi.getResidenceManager().getByLoc(player.location)
        if(residence == null) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.notFoundRes))
            return
        }
        val area: CuboidArea = residence.mainArea
        val cost:Double = when (getDirection(player)) {
            Direction.UP,Direction.DOWN -> area.highVector.blockX * area.highVector.blockZ * ConfigUtil.mainConfig.resCostBlock
            Direction.PLUSX, Direction.MINUSX -> area.highVector.blockZ * area.highVector.blockY * ConfigUtil.mainConfig.resCostBlock
            Direction.PLUSZ, Direction.MINUSZ -> area.highVector.blockX * area.highVector.blockY * ConfigUtil.mainConfig.resCostBlock
            null -> 0.0
        }

        if(guild.score < cost) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.guildScoreNoEnough))
            return
        }

        if(!player.isOp) {
            player.isOp = true
            Bukkit.dispatchCommand(player,"resadmin expand ${distance}")
            player.isOp = false
        } else Bukkit.dispatchCommand(player,"resadmin expand ${distance}")
        guild.score -= cost
        player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.expandResSuccessful))
    }
    companion object{
        private fun getDirection(player: Player): Direction? {
            var yaw = player.location.yaw.toInt()
            if (yaw < 0) {
                yaw += 360
            }

            yaw += 45
            yaw %= 360
            val facing = yaw / 90
            val pitch = player.location.pitch
            return if (pitch < -50.0f) {
                Direction.UP
            } else if (pitch > 50.0f) {
                Direction.DOWN
            } else if (facing == 1) {
                Direction.MINUSX
            } else if (facing == 3) {
                Direction.PLUSX
            } else if (facing == 2) {
                Direction.MINUSZ
            } else {
                if (facing == 0) Direction.PLUSZ else null
            }
        }
    }

}