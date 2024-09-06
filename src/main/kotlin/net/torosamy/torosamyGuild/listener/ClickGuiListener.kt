package net.torosamy.torosamyGuild.listener

import net.torosamy.torosamyGuild.manager.GuildManager
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
            if (event.inventory == guild.guildGUI.inventory) {
                event.isCancelled = true
                if (event.rawSlot == 8 && guild.residence != null) {
                    Bukkit.dispatchCommand(player,"res tp ${guild.residence}")
                    player.closeInventory()
                    return
                }
            }
        }
    }
}