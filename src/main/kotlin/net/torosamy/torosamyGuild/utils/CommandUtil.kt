package net.torosamy.torosamyGuild.utils

import net.torosamy.torosamyCore.TorosamyCore
import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.commands.AdminCommands
import net.torosamy.torosamyGuild.commands.PlayerCommands

class CommandUtil {
    companion object {
        private var torosamyCorePlugin: TorosamyCore = TorosamyGuild.plugin.server.pluginManager.getPlugin("TorosamyCore") as TorosamyCore
        fun registerCommand() {
            torosamyCorePlugin.getCommandManager().annotationParser.parse(PlayerCommands())
            torosamyCorePlugin.getCommandManager().annotationParser.parse(AdminCommands())
        }
    }
}