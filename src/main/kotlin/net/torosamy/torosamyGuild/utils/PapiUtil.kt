package net.torosamy.torosamyGuild.utils

import net.torosamy.torosamyGuild.papi.TorosamyGuildHolder

class PapiUtil {
    companion object {
        val torosamyGuildHolderPapi = TorosamyGuildHolder()

        fun reload() {
            unregister()
            register()
        }

        fun register() {
            torosamyGuildHolderPapi.register()
        }

        fun unregister() {
            torosamyGuildHolderPapi.unregister()
        }
    }
}