package net.torosamy.torosamyGuild.pojo

import java.sql.ResultSet

//class GuildPlayer private constructor(
class GuildPlayer (val username: String, val isOwner: Boolean, val guildId: String) {
    companion object {
        fun getPlayer(set: ResultSet): GuildPlayer {
            val username = set.getString("username")
            val isOwner = when (set.getInt("is_owner")) {
                1 -> true
                else -> false
            }
            val guild = set.getString("guild")
            return GuildPlayer(username, isOwner, guild)
        }
    }
}