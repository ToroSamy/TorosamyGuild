package net.torosamy.torosamyGuild.utils

import net.torosamy.torosamyCore.manager.ConfigManager
import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.config.LangConfig
import net.torosamy.torosamyGuild.config.MainConfig

class ConfigUtil {
    companion object {
        val mainConfig: MainConfig = MainConfig()
        val langConfig: LangConfig = LangConfig()

        private val mainConfigManager: ConfigManager = ConfigManager(mainConfig, TorosamyGuild.plugin,"","config.yml")
        private val langConfigManager: ConfigManager = ConfigManager(langConfig, TorosamyGuild.plugin,"","lang.yml")

        fun reloadConfig() {
            mainConfigManager.load()
            langConfigManager.load()
        }

        fun saveConfig() {
            mainConfigManager.save()
            langConfigManager.save()
        }

    }
}