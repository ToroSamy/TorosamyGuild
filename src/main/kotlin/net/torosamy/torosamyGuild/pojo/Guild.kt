package net.torosamy.torosamyGuild.pojo

import net.torosamy.torosamyCore.manager.ConfigManager
import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.config.GuildConfig
import net.torosamy.torosamyGuild.type.Color
import org.bukkit.Bukkit
import org.bukkit.Statistic
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.util.UUID
import kotlin.math.pow


class Guild private constructor(
    var enabled: Boolean,
    val uuid: String,
    var prefix: String,
    var owner: String,
    var residence: String?,
    val createTime: Long,
    var color: Color
) {
    val playerList: HashMap<String, Double> = HashMap()
    val applyPlayers: HashSet<String> = HashSet()
    var score: Double = 0.0
    //by lazy第一次访问后初始化
    val guildGUI: GuildGUI by lazy { GuildGUI(this) }
    /**
     * 从成员列表获取对应的玩家
     */
    fun getPlayer(name: String): String? {
        if (playerList.isEmpty()) return null
        return playerList.keys.find {it.equals(name,false) }
    }

    fun isOwner(player: Player): Guild? {
        if (this.owner == player.name) return this;
        return null;
    }

    fun getDisplayByPlayer(player: String): String? {
        if(owner == player) { return color.color + prefix }
        playerList.keys.forEach{name -> if(name == player) { return color.color + prefix } }
        return null
    }

    fun saveConfig() {
        ConfigManager.saveYaml(
            getConfigByGuild(this),
            YamlConfiguration(),
            "",
            TorosamyGuild.plugin.dataFolder.path + File.separator + "Guilds","${uuid}.yml")
    }
    //秒
    fun getPlayTimeMean():Double {
        var result = 0.0
        playerList.keys.forEach{ result += Bukkit.getOfflinePlayer(it).getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 }
        return result / playerList.size
    }

    fun getPlayTimeVariance(mean: Double):Double {
        var result = 0.0
        playerList.keys.forEach {result +=
            (Bukkit.getOfflinePlayer(it).getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 - mean)
                .pow(2.0)
        }

        return result / playerList.size
    }

    fun getLevel() : Long{
        val mean = getPlayTimeMean()
        val variance = getPlayTimeVariance(mean)

        val k = mean / 60.0
        val v = variance / 3600.0

        var result = score
        if(k > 1) result *= k
        if(v > 0.5) result /= v

        if(result > 1000) return (result / 1000.0).toLong()
        return 0
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

            guild.score = config.score

            for (applyPlayer in config.applyPlayers) {
                guild.applyPlayers.add(applyPlayer)
            }

            for (playerStr in config.playerList) {
                val split = playerStr.split(":")
                guild.playerList[split[0]] = split[1].toDouble()
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
            guildConfig.score = guild.score
            val playerList = ArrayList<String>()
            guild.playerList.forEach { playerList.add("${it.key}:${it.value}") }

            val applyPlayers = ArrayList<String>()
            guild.applyPlayers.forEach { applyPlayers.add(it) }

            guildConfig.playerList = playerList
            guildConfig.applyPlayers = applyPlayers
            return guildConfig
        }

        fun createGuild(owner: Player, prefix: String, color: Color): Guild {
            val uuid = UUID.randomUUID().toString()
            val createTime: Long = System.currentTimeMillis()
            val guild = Guild(true, uuid, prefix, owner.name, null, createTime, color)
            guild.playerList[owner.name] = 0.0
            return guild
        }


    }
}