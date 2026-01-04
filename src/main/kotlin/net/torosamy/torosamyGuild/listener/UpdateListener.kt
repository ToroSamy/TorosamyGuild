package net.torosamy.torosamyGuild.listener

import net.torosamy.torosamyGuild.api.TorosamyGuildAPI
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class UpdateListener : Listener {
    @EventHandler
    fun playerOnJoin(event: PlayerJoinEvent) {
        val guild = TorosamyGuildAPI.getGuild(event.player) ?: return
        
        TorosamyGuildAPI.clearApply(event.player.name)
        
        guild.autoRemoveRes()
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        val guild = TorosamyGuildAPI.getGuild(event.player) ?: return

        guild.autoRemoveRes()
        
        TorosamyGuildAPI.clearApply(event.player.name)
        
        guild.save()
    }
}