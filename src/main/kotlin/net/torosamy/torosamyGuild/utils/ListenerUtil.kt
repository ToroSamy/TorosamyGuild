package net.torosamy.torosamyGuild.utils

import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.listener.ClickGuiListener

class ListenerUtil {
    companion object{
        fun registerListener() {
            TorosamyGuild.plugin.server.pluginManager.registerEvents(ClickGuiListener(), TorosamyGuild.plugin)
        }
    }
}