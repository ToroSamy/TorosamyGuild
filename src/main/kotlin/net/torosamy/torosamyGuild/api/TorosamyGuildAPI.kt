package net.torosamy.torosamyGuild.api

import net.torosamy.torosamyCore.api.TorosamyCoreAPI
import net.torosamy.torosamyCore.config.ConfigFile
import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.pojo.Guild
import net.torosamy.torosamyGuild.type.Color
import net.torosamy.torosamyGuild.utils.ConfigUtil
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class TorosamyGuildAPI {
    companion object {
        private val guilds = HashMap<String, Guild>()
        
        private val rankedGuilds = ArrayList<Guild>()

        private var lastUpdateRankTimestamp: Long = 0
        
        fun getLastUpdateRankTimestamp(): Long {
            return lastUpdateRankTimestamp
        }
        
        fun isOwner(playerName: String): Boolean {
            guilds.values.forEach{
                if (it.isOwner(playerName)) {
                    return true
                }
            }
            return false
        }

        fun isMember(playerName: String): Boolean {
            guilds.values.forEach{
                if (it.isMember(playerName)) {
                    return true
                }
            }
            return false
        }
        
        fun hasGuild(playerName: String): Boolean {
            return  (isOwner(playerName) || isMember(playerName))
        }
        
        fun loadGuilds() {
            guilds.clear()
            for (it in TorosamyCoreAPI.getConfigs(TorosamyGuild.plugin, listOf("Guilds")).values) {
                val guild = Guild.generateInstance(it) ?: continue

                if (!guild.enabled) {
                    continue
                }
                
                guilds[guild.name] = guild
            }
            Bukkit.getConsoleSender().sendMessage(MessageUtil.format(ConfigUtil.langConfig.loadGuildsMessage.replace("%amount%", guilds.size.toString())))
        }

        fun updateRank(): List<Guild> {
            val nowTime = System.currentTimeMillis()
            
            val gapTime = (nowTime - lastUpdateRankTimestamp) / 1000
            if (gapTime < ConfigUtil.mainConfig.sortRankCooldown) {
                return rankedGuilds
            }

            val list: List<Guild> = guilds.values.sortedByDescending { 
                it.getLevel() 
            }
            
            rankedGuilds.clear()
            rankedGuilds.addAll(list)
            
            lastUpdateRankTimestamp = nowTime

            return list
        }
        
        fun saveGuilds() {
            guilds.values.forEach{
                it.save()
            }
        }
        
        fun getDefaultPrefix(): String {
            return Color.valueOf(ConfigUtil.mainConfig.defaultGuildConfig.color).color + ConfigUtil.mainConfig.noGuildHolder
        }
        
        fun getGuild(player: Player): Guild? {
            val name = player.name
            
            for (guild in guilds.values) {
                if (name == guild.owner) {
                    return guild
                }
                
                if (guild.isMember(name)) {
                    return guild
                }
            }
            return null;
        }
        
        fun getGuildsApplied(player: Player): ArrayList<Guild> {
            val result: ArrayList<Guild> = ArrayList()
        
            guilds.values.forEach{
                if (it.applied(player.name)) {
                    result.add(it)
                }
            }
            return result
        }

        fun getGuild(name: String): Guild? {
            return guilds[name]
        }

        fun addGuild(guild: Guild) {
            guilds[guild.name] = guild;
        }

        fun deleteGuild(guild: Guild) {
            guilds.remove(guild.name)
        }

        fun setHolder(message: String, guild: Guild):String {
            var residence: String = guild.residence
            
            if (residence.isEmpty()) {
                residence = ConfigUtil.langConfig.noResHolder
            }

            return message.replace("{prefix}",guild.getPrefix())
                .replace("{owner}", guild.owner)
                .replace("{level}", guild.getLevel().toString())
                .replace("{score}", guild.score.toString())
                .replace("{res}", residence)
        }
        fun clearApply(playerName: String) {
            guilds.values.forEach { 
                it.removeApply(playerName) 
            }
        }
        
    }
}