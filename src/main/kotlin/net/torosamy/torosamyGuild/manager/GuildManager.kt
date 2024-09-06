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
        var guildRank = Pair(0L, listOf<Guild>())

        fun loadGuilds() {
            guilds.clear()
            loadGuildData(ConfigManager.loadYamls(TorosamyGuild.plugin, "Guilds", ""))
            Bukkit.getConsoleSender()
                .sendMessage(MessageUtil.text("&a[服务器娘]&a插件 &eTorosamyGuild &a成功加载 &e${guilds.size} &a个公会喵~"))
        }

        fun saveGuilds() {
            guilds.values.forEach(Guild::saveConfig)
        }

        private fun loadGuildData(guildYmls: HashMap<String, ConfigurationSection>) {
            for (guildYml in guildYmls.values) {
                val guildConfig = GuildConfig()
                ConfigManager.loadData(guildConfig, guildYml as YamlConfiguration, "")
                //读取config后 如果未启用则跳过管理
                if (!guildConfig.enabled) continue
                guilds[guildConfig.uuid] = Guild.getGuildByConfig(guildConfig)
            }
        }


        /**
         * 查询用户所对应的Guild
         */
        fun getGuildByPlayer(name: String): Guild? {
            for (guild in guilds.values) {
                if (name == guild.owner) return guild
                if (guild.getPlayer(name) != null) return guild
            }
            return null;
        }

        fun getGuildByPrefix(prefix: String): Guild? {
            for (guild in guilds.values) {
                if (prefix == guild.prefix) return guild
            }
            return null
        }

        fun getDisplayByPlayer(player: String): String? {
            for (guild in guilds.values) {
                return guild.getDisplayByPlayer(player) ?: continue
            }
            return null;
        }

        fun addGuild(guild: Guild) {
            guilds[guild.uuid] = guild;
        }

        fun deleteGuild(guild: Guild) {
            guilds.remove(guild.uuid)
        }

        /**
         * 如果是公会主人则返回所在的公会
         */
        fun isOwner(player: Player): Guild? {
            for (guild in guilds.values) {
                if (guild.isOwner(player) != null) return guild
                continue
            }
            return null;
        }

        fun deleteApplyByPlayer(player: String) {
            guilds.values.forEach { it.applyPlayers.remove(player) }
        }


        fun sortGuilds() {
            val time: Long = (System.currentTimeMillis() / 1000)
            val list: List<Guild> = guilds.values.sortedByDescending { it.getLevel() }
            guildRank = Pair(time, list)
        }

    }
}