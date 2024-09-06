package net.torosamy.torosamyGuild.utils


import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.torosamy.torosamyCore.utils.MessageUtil
import org.bukkit.entity.Player

class HoverUtil {
    companion object {
        fun createCommandHover(text: String, command: String, hover: String): TextComponent {
            val message = TextComponent(MessageUtil.text(text))
            message.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, command)
            message.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder(MessageUtil.text(hover)).create())
            return message
        }

        fun sendCommandHover(player: Player, commandHover: TextComponent) {
            player.spigot().sendMessage(commandHover)
        }

    }
}