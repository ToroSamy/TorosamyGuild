package net.torosamy.torosamyGuild.commands


import me.clip.placeholderapi.PlaceholderAPI
import net.torosamy.torosamyCore.manager.ConfigManager
import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.manager.GuildManager
import net.torosamy.torosamyGuild.pojo.Guild
import net.torosamy.torosamyGuild.type.Color
import net.torosamy.torosamyGuild.utils.ConfigUtil
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.incendo.cloud.annotations.Argument
import org.incendo.cloud.annotations.Command
import org.incendo.cloud.annotations.CommandDescription
import org.incendo.cloud.annotations.Permission

class PlayerCommands {
    @Command("guild create <prefix>", requiredSender = Player::class)
    @Permission("torosamyguild.create")
    @CommandDescription("创建公会")
    fun createGuild(sender: CommandSender, @Argument("prefix") prefix: String) {
        val player = sender as Player

        if (GuildManager.getPlayer(player.name) != null) {
            player.sendMessage(
                MessageUtil.text(PlaceholderAPI.setPlaceholders(player, ConfigUtil.langConfig.alreadyHasGuild)
                )
            )
            return
        }
        if (prefix.length < ConfigUtil.mainConfig.prefixMinLength) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.prefixTooShort.replace("{prefix}", prefix)))
            return
        }
        if (prefix.length > ConfigUtil.mainConfig.prefixMaxLength) {
            player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.prefixTooLong.replace("{prefix}", prefix)))
            return
        }

        //TODO create Color Menu
        val tempColor = Color.YELLOW

        val guild = Guild.createGuild(player, prefix, tempColor)
        GuildManager.addGuild(guild)
        //TODO 创建文件 用uuid给文件命名 使用户可以更改前缀
        val config = Guild.createConfig(guild)

            ConfigManager.loadYaml(TorosamyGuild.plugin, "Guilds",
                "${guild.uuid}.yml",config)
        player.sendMessage(MessageUtil.text(ConfigUtil.langConfig.createSuccessful.replace("{prefix}", prefix)))
    }
}