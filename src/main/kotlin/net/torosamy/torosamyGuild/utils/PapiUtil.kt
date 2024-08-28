package net.torosamy.torosamyGuild.utils

import net.torosamy.torosamyGuild.papi.GuildPrefix

class PapiUtil {
    companion object {
        val guildPrefixPapi = GuildPrefix()

        fun reload() {
            unregister()
            register()
        }

        fun register() {
            guildPrefixPapi.register()
        }

        fun unregister() {
            guildPrefixPapi.unregister()
        }
    }
}