package net.torosamy.torosamyGuild.pojo

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class GuildGUIHolder(val prefix: String) : InventoryHolder {
    override fun getInventory(): Inventory {
        throw UnsupportedOperationException("This InventoryHolder is only used as a marker.")
    }
    
    companion object {
        fun isGuildGUI(inventory: Inventory): Boolean {
            return inventory.holder is GuildGUIHolder
        }
    }
}