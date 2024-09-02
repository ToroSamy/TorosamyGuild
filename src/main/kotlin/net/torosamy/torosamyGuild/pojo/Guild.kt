package net.torosamy.torosamyGuild.pojo

import net.torosamy.torosamyGuild.config.GuildConfig
import net.torosamy.torosamyGuild.type.Color
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import java.util.UUID


class Guild private constructor(
    val uuid: String,
    val prefix: String,
    val owner: Player,
    val residence: String?,
    val createTime: Long,
    val color: Color) {
    val playerList: ArrayList<Player> = ArrayList()
    val applyPlayers: ArrayList<Player> = ArrayList()

    fun getPlayer(name: String): Player? {
        if (playerList.isEmpty()) return null
        return playerList.find { it.name.equals(name, false) }
    }

    fun isOwner(player: Player): Boolean {
        return this.owner == player
    }


    companion object {
        fun createGuild(config: GuildConfig): Guild {
            val uuid = config.uuid
            val prefix = config.prefix
            val owner = Bukkit.getPlayer(config.owner)!!
            val residence = config.res
            val createTime = config.createTime
            val color = Color.valueOf(config.color)

            val guild = Guild(uuid, prefix, owner, residence, createTime, color)

            for (applyPlayer in config.applyPlayers) {
                Bukkit.getPlayer(applyPlayer)?.let { guild.applyPlayers.add(it) }
            }

            for (playerStr in config.playerList) {
                Bukkit.getPlayer(playerStr)?.let { guild.playerList.add(it) }
            }
            return guild
        }

        fun createConfig(guild: Guild): GuildConfig {
            val guildConfig = GuildConfig()
            guildConfig.uuid = guild.uuid
            guildConfig.prefix = guild.prefix
            guildConfig.owner = guild.owner.name
            guildConfig.res = guild.residence;
            guildConfig.createTime = guild.createTime
            guildConfig.color = guild.color.toString()

            val playerList = ArrayList<String>()
            for (player in guild.playerList) {
                playerList.add(player.name)
            }

            val applyPlayers = ArrayList<String>()
            for (applyPlayer in guild.applyPlayers) {
                applyPlayers.add(applyPlayer.name)
            }

            guildConfig.playerList = playerList
            guildConfig.applyPlayers = applyPlayers

            return guildConfig
        }

        fun createGuild(owner: Player, prefix: String, color: Color): Guild {
            val uuid = UUID.randomUUID().toString()
            val createTime: Long = System.currentTimeMillis()
            return Guild(uuid, prefix, owner, null, createTime, color)
        }
    }
}