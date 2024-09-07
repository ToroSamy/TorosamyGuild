package net.torosamy.torosamyGuild.utils

import net.torosamy.torosamyCore.TorosamyCore
import net.torosamy.torosamyGuild.commands.AdminCommands
import net.torosamy.torosamyGuild.commands.OwnerCommands
import net.torosamy.torosamyGuild.commands.PlayerCommands
import net.torosamy.torosamyGuild.commands.ResCommands

class CommandUtil {
    companion object {
        fun registerCommand() {
            TorosamyCore.commanderManager.annotationParser.parse(PlayerCommands())
            TorosamyCore.commanderManager.annotationParser.parse(AdminCommands())
            TorosamyCore.commanderManager.annotationParser.parse(OwnerCommands())
            TorosamyCore.commanderManager.annotationParser.parse(ResCommands())
        }
    }
}