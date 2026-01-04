package net.torosamy.torosamyGuild.listener

import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.api.TorosamyGuildAPI
import net.torosamy.torosamyGuild.pojo.GuildGUIHolder
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

        if (!GuildGUIHolder.isGuildGUI(event.inventory)) {
            return
        }

        event.isCancelled = true

        if (event.rawSlot != 4) {
            return
        }

        val prefix = (event.inventory.holder as GuildGUIHolder).prefix

        val guild = TorosamyGuildAPI.getGuild(prefix) ?: return

        if (guild.residence.isEmpty()) {
            player.sendMessage(MessageUtil.format(ConfigUtil.langConfig.notFoundRes))
            player.closeInventory()
            return
        }

        Bukkit.dispatchCommand(player,"res tp ${guild.residence}")
        player.closeInventory()
    }
}