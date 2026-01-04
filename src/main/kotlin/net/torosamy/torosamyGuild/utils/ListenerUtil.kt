package net.torosamy.torosamyGuild.utils

import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.listener.ClickGuiListener
import net.torosamy.torosamyGuild.listener.UpdateListener

class ListenerUtil {
    companion object{
        fun registerListener() {
            TorosamyGuild.plugin.server.pluginManager.registerEvents(ClickGuiListener(), TorosamyGuild.plugin)
            TorosamyGuild.plugin.server.pluginManager.registerEvents(UpdateListener(), TorosamyGuild.plugin)
        }
    }
}