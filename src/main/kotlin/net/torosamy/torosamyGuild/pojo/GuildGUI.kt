package net.torosamy.torosamyGuild.pojo

import com.google.common.collect.ImmutableMultimap
import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.utils.ConfigUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

class GuildGUI(val guild: Guild) {
    val inventory = Bukkit.createInventory(null, 9, MessageUtil.text(ConfigUtil.langConfig.guildGuiTitle))

    private fun setHolder(lore: String):String {
        return lore.replace("{prefix}",guild.color.color+guild.prefix)
            .replace("{owner}",guild.owner)
            .replace("{level}",guild.getLevel().toString())
            .replace("{score}", guild.score.toString())
            .replace("{res}",guild.residence?:"暂无")
    }

    fun openGui(player: Player) {
        player.closeInventory()

        val basicInfoItem = ItemStack(Material.valueOf(ConfigUtil.mainConfig.guiBasicInfoItem))
        val basicInfoMeta = basicInfoItem.itemMeta
        basicInfoMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        basicInfoMeta.attributeModifiers = ImmutableMultimap.of()
        basicInfoMeta.setDisplayName(MessageUtil.text(ConfigUtil.langConfig.basicInfoDisplay))
        basicInfoMeta.lore = ConfigUtil.langConfig.guildBasicInfo.map { MessageUtil.text(setHolder(it)) }
        basicInfoItem.setItemMeta(basicInfoMeta)
        inventory.setItem(0, basicInfoItem)

        val resTpItem = ItemStack(Material.valueOf(ConfigUtil.mainConfig.guiVisitItem))
        val resTpMeta = resTpItem.itemMeta
        resTpMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        resTpMeta.attributeModifiers = ImmutableMultimap.of()
        resTpMeta.setDisplayName(MessageUtil.text(ConfigUtil.langConfig.guildVisit))
        resTpItem.setItemMeta(resTpMeta)
        inventory.setItem(8, resTpItem)

        val memberItem = ItemStack(Material.valueOf(ConfigUtil.mainConfig.memberDonationItem))
        val memberMeta = memberItem.itemMeta
        memberMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        memberMeta.attributeModifiers = ImmutableMultimap.of()
        memberMeta.setDisplayName(MessageUtil.text(ConfigUtil.langConfig.memberDonation))
        val list = ArrayList<String>()
        for (entry in guild.playerList) { list.add(MessageUtil.text(" - &6${entry.key}&f: &e${entry.value}")) }
        memberMeta.lore = list
        memberItem.setItemMeta(memberMeta)
        inventory.setItem(7, memberItem)

        player.openInventory(inventory)
    }
}