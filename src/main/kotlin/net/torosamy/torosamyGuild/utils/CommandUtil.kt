package net.torosamy.torosamyGuild.utils

import net.torosamy.torosamyCore.commands.CommandManager
import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.commands.AdminCommands
import net.torosamy.torosamyGuild.commands.OwnerCommands
import net.torosamy.torosamyGuild.commands.PlayerCommands
import net.torosamy.torosamyGuild.commands.ResCommands

class CommandUtil {
    companion object {
        private val commanderManager: CommandManager = CommandManager(TorosamyGuild.plugin)

        public val ADMIN_COMMANDS: AdminCommands = AdminCommands();
        public val PLAYER_COMMANDS: PlayerCommands = PlayerCommands();
        public val OWNER_COMMANDS: OwnerCommands = OwnerCommands();
        public val RES_COMMANDS: ResCommands = ResCommands();

        fun registerCommand() {
            commanderManager.annotationParser.parse(ADMIN_COMMANDS)
            commanderManager.annotationParser.parse(PLAYER_COMMANDS)
            commanderManager.annotationParser.parse(OWNER_COMMANDS)
            commanderManager.annotationParser.parse(RES_COMMANDS)
        }
    }
}