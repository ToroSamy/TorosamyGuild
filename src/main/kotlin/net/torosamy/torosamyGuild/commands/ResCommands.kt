package net.torosamy.torosamyGuild.commands

import com.bekvon.bukkit.residence.Residence
import com.bekvon.bukkit.residence.api.ResidenceApi
import com.bekvon.bukkit.residence.commands.expand
import com.bekvon.bukkit.residence.commands.resadmin
import com.bekvon.bukkit.residence.protection.ClaimedResidence
import com.bekvon.bukkit.residence.protection.CuboidArea
import com.bekvon.bukkit.residence.selection.SelectionManager.Direction
import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.api.TorosamyGuildAPI
import net.torosamy.torosamyGuild.utils.ConfigUtil
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission


class ResCommands {
    @Command("guild res create <name>", requiredSender = Player::class)
    @Permission("torosamyguild.res.create")
    @CommandDescription("创建公会领地")
    fun createRes(sender: CommandSender, @Argument("name") name: String)  {
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
        

        if (guild.hasResidence()) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.alreadyHasRes))
            return
        }
        
        val cuboidArea = Residence.getInstance().selectionManager.getSelectionCuboid(player) 
        
        if (cuboidArea == null || cuboidArea.lowLocation == null || cuboidArea.highLocation == null ||
            Residence.getInstance().selectionManager.getPlayerLoc1(player) == null ||
            Residence.getInstance().selectionManager.getPlayerLoc2(player) == null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.resNotSelect));
            return
        }
            
        
        val cost = cuboidArea.size * ConfigUtil.mainConfig.resCostBlock

        if (guild.score < cost) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.guildScoreNoEnough))
            return
        }
        
        if (!Residence.getInstance().residenceManager.addResidence(player, name, true)) {
            return
        }
        
        val residence = ResidenceApi.getResidenceManager().getByName(name)
        
        if (residence == null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.guildCreateFail));
            return
        }

        
        guild.score -= cost
        guild.residence = name
        guild.save()
        player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.createResSuccessful))
    }
    @Command("guild res set <name>")
    @Permission("torosamyguild.res.set")
    @CommandDescription("将自己的一个领地设为公会领地")
    fun setRes(player: Player, @Argument("name") name: String) {
        val guild = TorosamyGuildAPI.getGuild(player)

        if (guild == null || !guild.isOwner(player.name)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }
        
        val residence = Residence.getInstance().residenceManager.getByName(name)
        
        if (residence == null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundRes))
            return
        }

        if (!residence.isOwner(player)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundRes))
            return
        }
        
        guild.residence = name
        player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.createResSuccessful))
    }

    @Command("guild res expand <distance>")
    @Permission("torosamyguild.res.expand")
    @CommandDescription("扩展公会")
    fun expandRes(player: Player, @Argument("distance") distance: Int)  {
        if (distance <= 0) {
            player.sendMessage(MessageUtil.component(ConfigUtil.langConfig.amountError))
            return
        }
        
        val guild = TorosamyGuildAPI.getGuild(player)

        if (guild == null || !guild.isOwner(player.name)) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.isNotGuildOwner))
            return
        }

        if (!guild.hasResidence()) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundRes))
            return
        }

        val residence: ClaimedResidence? = ResidenceApi.getResidenceManager().getByLoc(player.location)
        if(residence == null) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundRes))
            return
        }
        val area: CuboidArea = residence.mainArea

        Residence.getInstance().selectionManager.placeLoc1(player, area.highLocation, false);
        Residence.getInstance().selectionManager.placeLoc2(player, area.lowLocation, false);

        val oldSize = Residence.getInstance().selectionManager.getSelection(player).baseArea.size
        
        Residence.getInstance().selectionManager.modify(player, false, distance)

        val newSize = Residence.getInstance().selectionManager.getSelection(player).baseArea.size

        val cost = (newSize - oldSize) * ConfigUtil.mainConfig.resCostBlock
        
        player.sendMessage(MessageUtil.component(ConfigUtil.langConfig.expandRemind
            .replace("%old%", oldSize.toString())
            .replace("%new%", newSize.toString())
            .replace("%cost%", cost.toString())
        ))
        
        
        if(guild.score < cost) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.guildScoreNoEnough))
            return
        }

        if (!expand().perform(Residence.getInstance(), player, arrayOf(distance.toString()), true)) {
            return
        }


        guild.score -= cost
        player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.expandResSuccessful))
    }
}