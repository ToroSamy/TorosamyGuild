package net.torosamy.torosamyGuild

import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.utils.CommandUtil
import net.torosamy.torosamyGuild.utils.ConfigUtil
import net.torosamy.torosamyGuild.utils.PapiUtil
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class TorosamyGuild : JavaPlugin() {
    companion object{lateinit var plugin: TorosamyGuild}
    override fun onEnable() {
        plugin = this
        CommandUtil.registerCommand()
        ConfigUtil.reloadConfig()
        PapiUtil.register()
//        ListenerUtil.registerListener()

        Bukkit.getConsoleSender().sendMessage(MessageUtil.text("&a[服务器娘]&a插件 &eTorosamyGuild &a成功开启喵~"))
        Bukkit.getConsoleSender().sendMessage(MessageUtil.text("&a[服务器娘]&a作者 &eTorosamy|yweiyang"))
    }

    override fun onDisable() {
        ConfigUtil.saveConfig()


        Bukkit.getConsoleSender().sendMessage(MessageUtil.text("&a[服务器娘]&c插件 &eTorosamyGuild &c成功关闭喵~"))
        Bukkit.getConsoleSender().sendMessage(MessageUtil.text("&a[服务器娘]&c作者 &eTorosamy|yweiyang"))
    }
}