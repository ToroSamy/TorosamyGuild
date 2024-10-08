package net.torosamy.torosamyGuild



import net.milkbowl.vault.economy.Economy
import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.manager.GuildManager
import net.torosamy.torosamyGuild.utils.*
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class TorosamyGuild : JavaPlugin() {
    companion object{
        lateinit var plugin: TorosamyGuild
        lateinit var economy: Economy
    }
    override fun onEnable() {
        plugin = this
        val registration = server.servicesManager.getRegistration(Economy::class.java)
        if (registration != null) { economy = registration.provider }
        ListenerUtil.registerListener()
        CommandUtil.registerCommand()
        ConfigUtil.reloadConfig()
        PapiUtil.register()
        SqlUtil.initDatabase()
        GuildManager.loadGuilds()
        SchedulerUtil.registerScheduler()


        Bukkit.getConsoleSender().sendMessage(MessageUtil.text("&a[服务器娘]&a插件 &eTorosamyGuild &a成功开启喵~"))
        Bukkit.getConsoleSender().sendMessage(MessageUtil.text("&a[服务器娘]&a作者 &eTorosamy|yweiyang"))
    }

    override fun onDisable() {
        ConfigUtil.saveConfig()
        GuildManager.saveGuilds()

        Bukkit.getConsoleSender().sendMessage(MessageUtil.text("&a[服务器娘]&c插件 &eTorosamyGuild &c成功关闭喵~"))
        Bukkit.getConsoleSender().sendMessage(MessageUtil.text("&a[服务器娘]&c作者 &eTorosamy|yweiyang"))
    }
}