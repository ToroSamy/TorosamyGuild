package net.torosamy.torosamyGuild.utils

import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.scheduler.AutoSaveTask
import org.bukkit.scheduler.BukkitTask

class SchedulerUtil {
    companion object{
        private lateinit var autoSaveTask: BukkitTask

        fun registerScheduler() {
            registerAutoSave()
        }

        fun registerAutoSave() {
            if (::autoSaveTask.isInitialized && !autoSaveTask.isCancelled) {
                autoSaveTask.cancel()
            }
            if (!ConfigUtil.mainConfig.autoSave.enabled) return
            autoSaveTask = AutoSaveTask().runTaskTimer(TorosamyGuild.plugin, 0L, 20L)
        }
    }
}