package net.torosamy.torosamyGuild.scheduler

import net.torosamy.torosamyGuild.api.TorosamyGuildAPI
import net.torosamy.torosamyGuild.utils.ConfigUtil
import org.bukkit.scheduler.BukkitRunnable

class AutoSaveTask:BukkitRunnable() {
    var remainTime = ConfigUtil.mainConfig.autoSave.minutes * 60L

    override fun run() {
        if(remainTime > 0) {
            remainTime--
        }
        else {
            remainTime = ConfigUtil.mainConfig.autoSave.minutes * 60L
            TorosamyGuildAPI.saveGuilds()
        }
    }
}