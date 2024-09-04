package net.torosamy.torosamyGuild.pojo

import net.torosamy.torosamyCore.manager.ConfigManager
import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.config.GuildConfig
import net.torosamy.torosamyGuild.type.Color
import org.bukkit.Bukkit
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID


class Guild private constructor(
    var enabled: Boolean,
    val uuid: String,
    var prefix: String,
    var owner: String,
    var residence: String?,
    val createTime: Long,
    var color: Color) {
    val playerList: HashSet<String> = HashSet()
    val applyPlayers: HashSet<String> = HashSet()

    /**
     * 从成员列表获取对应的玩家
     */
    fun getPlayer(name: String): String? {
        if (playerList.isEmpty()) return null
        return playerList.find { it.equals(name, false) }
    }

    fun isOwner(player: Player): Guild? {
        if (this.owner == player.name) return this;
        return null;
    }

    fun getDisplayByPlayer(player: String): String? {
        if(owner == player) { return color.color + prefix }
        playerList.forEach{name -> if(name == player) { return color.color + prefix } }
        return null
    }

    fun saveConfig() {
        ConfigManager.saveYaml(
            getConfigByGuild(this),
            YamlConfiguration(),
            "",
            TorosamyGuild.plugin.dataFolder.path + File.separator + "Guilds","${uuid}.yml")
    }

    companion object {
        fun getGuildByConfig(config: GuildConfig): Guild {
            val enabled = config.enabled
            val uuid = config.uuid
            val prefix = config.prefix
            val owner = config.owner
            val residence = config.res
            val createTime = config.createTime.toLong()
            val color = Color.valueOf(config.color)

            val guild = Guild(enabled,uuid, prefix, owner, residence, createTime, color)

            for (applyPlayer in config.applyPlayers) {
                guild.applyPlayers.add(applyPlayer)
            }

            for (playerStr in config.playerList) {
                guild.playerList.add(playerStr)
            }
            return guild
        }

        fun getConfigByGuild(guild: Guild): GuildConfig {
            val guildConfig = GuildConfig()
            guildConfig.enabled = guild.enabled
            guildConfig.uuid = guild.uuid
            guildConfig.prefix = guild.prefix
            guildConfig.owner = guild.owner
            guildConfig.res = guild.residence
            guildConfig.createTime = guild.createTime.toString()
            guildConfig.color = guild.color.toString()

            val playerList = ArrayList<String>()
            guild.playerList.forEach { playerList.add(it) }

            val applyPlayers = ArrayList<String>()
            guild.applyPlayers.forEach { applyPlayers.add(it) }

            guildConfig.playerList = playerList
            guildConfig.applyPlayers = applyPlayers
            return guildConfig
        }


        fun createConfig(guild: Guild): GuildConfig {
            val guildConfig = GuildConfig()
            guildConfig.enabled = guild.enabled
            guildConfig.uuid = guild.uuid
            guildConfig.prefix = guild.prefix
            guildConfig.owner = guild.owner
            guildConfig.res = guild.residence;
            guildConfig.createTime = guild.createTime.toString()
            guildConfig.color = guild.color.toString()
            //将HashSet转为List储存到Config中
            val playerList = ArrayList<String>()
            for (player in guild.playerList) {
                playerList.add(player)
            }

            val applyPlayers = ArrayList<String>()
            for (applyPlayer in guild.applyPlayers) {
                applyPlayers.add(applyPlayer)
            }

            guildConfig.playerList = playerList
            guildConfig.applyPlayers = applyPlayers

            return guildConfig
        }

        fun createGuild(owner: Player, prefix: String, color: Color): Guild {
            val uuid = UUID.randomUUID().toString()
            val createTime: Long = System.currentTimeMillis()
            return Guild(true, uuid, prefix, owner.name, null, createTime, color)
        }
    }
}