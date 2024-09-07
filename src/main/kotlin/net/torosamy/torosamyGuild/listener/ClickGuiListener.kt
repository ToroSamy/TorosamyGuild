package net.torosamy.torosamyGuild.listener

import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.manager.GuildManager
import net.torosamy.torosamyGuild.utils.ConfigUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class ClickGuiListener : Listener {
    @EventHandler
    fun onCancel(event: InventoryClickEvent) {
        val player = event.whoClicked as Player

        for (guild in GuildManager.guilds.values) {
            if (event.inventory != guild.guildGUI.inventory) continue

            event.isCancelled = true

            if (event.rawSlot != 4) continue

            if(guild.residence.isNotEmpty()) {
                Bukkit.dispatchCommand(player,"res tp ${guild.residence}")
                player.closeInventory()
                return
            }

            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.notFoundRes))
            player.closeInventory()
        }
    }
}