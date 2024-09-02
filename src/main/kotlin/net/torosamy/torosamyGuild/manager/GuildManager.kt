package net.torosamy.torosamyGuild.manager

import net.torosamy.torosamyCore.manager.ConfigManager
import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.config.GuildConfig
import net.torosamy.torosamyGuild.pojo.Guild
import org.bukkit.Bukkit
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player

class GuildManager {
    companion object {
        val guilds = HashMap<String, Guild>()

        fun loadGuilds() {
            guilds.clear()
            loadGuildData(ConfigManager.loadYamls(TorosamyGuild.plugin, "Guilds", ""))
            Bukkit.getConsoleSender()
                .sendMessage(MessageUtil.text("&a[服务器娘]&a插件 &eTorosamyGuild &a成功加载 &e${guilds.size} &a个公会喵~"))
        }

        private fun loadGuildData(guildYmls: HashMap<String, ConfigurationSection>) {
            for (guildYml in guildYmls.values) {
                val guildConfig = GuildConfig()
                ConfigManager.loadData(guildConfig,guildYml as YamlConfiguration , "");
                guilds[guildConfig.uuid] = Guild.createGuild(guildConfig)
            }
        }

        fun getPlayer(name: String): Player? {
            for (guild in guilds.values) {
                if (name == guild.owner?.name) return Bukkit.getPlayer(name);
                return guild.getPlayer(name)
            }
            return null;
        }

        fun addGuild(guild: Guild) {
            guilds[guild.uuid] = guild;
        }

    }
}