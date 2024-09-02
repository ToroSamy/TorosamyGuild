package net.torosamy.torosamyGuild.utils

import net.torosamy.torosamyCore.TorosamyCore
import net.torosamy.torosamyGuild.commands.AdminCommands
import net.torosamy.torosamyGuild.commands.PlayerCommands

class CommandUtil {
    companion object {
        fun registerCommand() {
            TorosamyCore.commanderManager.annotationParser.parse(PlayerCommands())
            TorosamyCore.commanderManager.annotationParser.parse(AdminCommands())
        }
    }
}