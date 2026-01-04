package net.torosamy.torosamyGuild.papi

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.api.TorosamyGuildAPI
import net.torosamy.torosamyGuild.type.Color
import net.torosamy.torosamyGuild.utils.ConfigUtil
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class TorosamyGuildHolder : PlaceholderExpansion() {
    override fun getIdentifier(): String {
        return "guild"
    }

    override fun getAuthor(): String {
        return "Torosamy"
    }

    override fun getVersion(): String {
        return TorosamyGuild.plugin.description.version
    }

    override fun onRequest(player: OfflinePlayer?, params: String): String? {
        if ("prefix" == params) {
            val guild = TorosamyGuildAPI.getGuild((player as Player))
            if (guild != null) {
                return guild.getPrefix()
            }
            return TorosamyGuildAPI.getDefaultPrefix()
        }
        
        if ("name" == params) {
            val guild = TorosamyGuildAPI.getGuild((player as Player))
            
            if (guild != null) {
                return guild.name
            }
            return ConfigUtil.mainConfig.noGuildHolder
        }
        
        return null
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        return onRequest(player, params)
    }
}