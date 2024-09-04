package net.torosamy.torosamyGuild.utils

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.torosamy.torosamyGuild.pojo.GuildPlayer
import java.sql.PreparedStatement

class SqlUtil {
    companion object {
        private lateinit var dataSource: HikariDataSource

        fun initDatabase(): Unit {
            if (!ConfigUtil.mainConfig.database.enabled) return

            val host = ConfigUtil.mainConfig.database.host;
            val port = ConfigUtil.mainConfig.database.port;
            val username = ConfigUtil.mainConfig.database.username;
            val password = ConfigUtil.mainConfig.database.password;
            val database = ConfigUtil.mainConfig.database.database;

            val databaseConfig = HikariConfig()

            databaseConfig.driverClassName = "com.mysql.cj.jdbc.Driver"
            databaseConfig.jdbcUrl =
                "jdbc:mysql://$host:$port/$database?useUnicode=true&characterEncoding=utf8&useSSL=false"
            databaseConfig.username = username
            databaseConfig.password = password

            dataSource = HikariDataSource(databaseConfig)
        }

        fun queryPlayer(playerName: String): GuildPlayer? {
            val prepareStatement: PreparedStatement = dataSource.connection.prepareStatement("select * from player where username = ?")
            prepareStatement.setString(1,playerName)
            val set = prepareStatement.executeQuery()
            if (!set.next()) return null

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


//        fun initSqlManager() {
//            val driver = ConfigUtil.mainConfig.database.driver
//            val username = ConfigUtil.mainConfig.database.username
//            val password = ConfigUtil.mainConfig.database.password
//
//            val host = ConfigUtil.mainConfig.database.host
//            val port = ConfigUtil.mainConfig.database.port
//            val database = ConfigUtil.mainConfig.database.database
//            val url = "jdbc:mysql://${host}:${port}/${database}"
//            println(url)
//            val conn = DriverManager.getConnection(url, username, password)
//            val statement = conn.createStatement()
//            val sql = "select * from player"
//            val resultSet = statement.executeQuery(sql)
//            while (resultSet.next()) {
//                val id = resultSet.getInt("id")
//                val name = resultSet.getString("username")
//                val guild = resultSet.getString("guild")
//                println("$id $name $guild")
//            }
//
//            Bukkit.getConsoleSender().sendMessage(MessageUtil.text("&a[服务器娘]&a插件 &eTorosamyGuild &a连接数据库成功"))
//        }