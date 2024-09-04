package net.torosamy.torosamyGuild.papi

import me.clip.placeholderapi.expansion.PlaceholderExpansion
import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.manager.GuildManager
import net.torosamy.torosamyGuild.utils.ConfigUtil
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class GuildPrefix : PlaceholderExpansion() {
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
        if("prefix" != params) return null
        val guildPrefix = GuildManager.getDisplayByPlayer((player as Player).name)
        if(guildPrefix != null) return guildPrefix;
        return ConfigUtil.langConfig.defaultPrefix
    }

    override fun onPlaceholderRequest(player: Player?, params: String): String? {
        if("prefix" != params) return null
        val guildPrefix = GuildManager.getDisplayByPlayer((player as Player).name)
        if(guildPrefix != null) return guildPrefix;
        return ConfigUtil.langConfig.defaultPrefix
    }
}