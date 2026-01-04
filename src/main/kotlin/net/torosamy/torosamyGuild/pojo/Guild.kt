package net.torosamy.torosamyGuild.pojo

import com.bekvon.bukkit.residence.Residence
import com.bekvon.bukkit.residence.api.ResidenceApi
import com.google.common.collect.ImmutableMultimap
import net.torosamy.torosamyCore.config.ConfigFile
import net.torosamy.torosamyCore.utils.MessageUtil
import net.torosamy.torosamyGuild.TorosamyGuild
import net.torosamy.torosamyGuild.api.TorosamyGuildAPI
import net.torosamy.torosamyGuild.type.Color
import net.torosamy.torosamyGuild.utils.ConfigUtil
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Statistic
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet
import kotlin.math.pow


class Guild {
    private val donates: HashMap<String, Double> = HashMap()
    private val applies: HashSet<String> = HashSet()
    private val members: HashSet<String> = HashSet()
    val uuid: String
    val createTime: Long
    var holder: GuildGUIHolder
    var enabled: Boolean
    var name: String
    var owner: String
    var residence: String
    var color: Color
    var score: Double
    
//    val guildGUI: GuildGUI by lazy { GuildGUI(this) } // TODO
    public constructor(player: Player, name: String) {
        this.owner = player.name
        this.name = name
        this.color = Color.valueOf(ConfigUtil.mainConfig.defaultGuildConfig.color)
        this.enabled = true
        this.uuid = UUID.randomUUID().toString()
        this.createTime = System.currentTimeMillis()
        this.residence = ""
        this.score = 0.0
        this.holder = GuildGUIHolder(name)
        this.donates[owner] = 0.0
    }
    
    public fun save() {
        val file = ConfigFile(TorosamyGuild.plugin, uuid + ".yml", listOf("Guilds")).getFile(false)
        val config = generateConfig()
        
        config.save(file)
    }

    private constructor(config: ConfigurationSection) {
        this.enabled = config.getBoolean("enabled", true)
        this.score = config.getDouble("score", ConfigUtil.mainConfig.defaultGuildConfig.score)
        this.name = config.getString("name", null)!!
        this.owner = config.getString("owner", null)!!
        this.residence = config.getString("residence", null) ?: ""
        
        this.members.clear()
        this.members.addAll(config.getStringList("members"))

        val createTime = config.getString("create-time", null)
        if (createTime == null) {
            this.createTime = System.currentTimeMillis()
        }else {
            this.createTime = createTime.toLong()
        }

        this.color = Color.valueOf(
            config.getString("color", null) ?: ConfigUtil.mainConfig.defaultGuildConfig.color
        )

        this.uuid = config.getString("uuid", null) ?: UUID.randomUUID().toString()

        applies.addAll(config.getStringList("applies"))
        
        config.getStringList("donates").forEach {
            val split = it.split(":")

            val name = split[0]
            val score = split[1].toDouble()
            
            if (members.contains(name) || name == owner) {
                donates[name] = score
            }

        }
        
        this.holder = GuildGUIHolder(name)
    }
    
    
    fun donate(playerName: String, score: Double): Boolean {
        return donate(playerName, score, false)
    }

    fun donate(playerName: String, score: Double, isAdmin: Boolean): Boolean {
        if (isAdmin) {
            this.score += score
            return true
        }
        
        val memberScore = donates[playerName] ?: return false
        
        donates[playerName] = memberScore + score
        this.score += score

        return true
    }

    fun lookApplies(): ArrayList<String> {
        return ArrayList(applies)
    }
    
    fun isMember(name: String): Boolean {
        if (members.isEmpty()) {
            return false
        }
        return members.contains(name)
    }
    
    fun quit(playerName: String): Boolean {
        if (!members.contains(playerName)) {
            return false
        }
        members.remove(playerName)
        return true
    }
    

    fun applied(playerName: String): Boolean {
        return applies.contains(playerName)
    }

    fun removeApply(playerName: String): Boolean {
        return applies.remove(playerName)
    }
    
    fun generateGUI(): Inventory {
        val inventory = Bukkit.createInventory(holder, 9, name)
        
        val basicInfoItem = ItemStack(Material.valueOf(ConfigUtil.mainConfig.guiBasicInfoItem))
        val basicInfoMeta = basicInfoItem.itemMeta

        basicInfoMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        basicInfoMeta.attributeModifiers = ImmutableMultimap.of()

        basicInfoMeta.setDisplayName(MessageUtil.format(ConfigUtil.langConfig.basicInfoDisplay))

        basicInfoMeta.lore = ConfigUtil.langConfig.guildBasicInfo.map {
            MessageUtil.format(TorosamyGuildAPI.setHolder(it, this))
        }

        basicInfoItem.setItemMeta(basicInfoMeta)
        inventory.setItem(4, basicInfoItem)

        val memberItem = ItemStack(Material.valueOf(ConfigUtil.mainConfig.memberDonationItem))

        val memberMeta = memberItem.itemMeta
        memberMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
        memberMeta.attributeModifiers = ImmutableMultimap.of()
        memberMeta.setDisplayName(MessageUtil.format(ConfigUtil.langConfig.memberDonation))

        val list = ArrayList<String>()
        for (entry in donates) {
            list.add(MessageUtil.format(" - &6${entry.key}&f: &e${entry.value}"))
        }
        memberMeta.lore = list

        memberItem.setItemMeta(memberMeta)
        inventory.setItem(8, memberItem)
        
        return inventory
    }
    fun autoRemoveRes(): Boolean {
        if (residence.isEmpty()) {
            return false
        }
        
        val claimedResidence = ResidenceApi.getResidenceManager().getByName(residence)

        if (claimedResidence == null) {
            clearResidence()
            return true
        }

        if (claimedResidence.isOwner(owner)) {
            return false
        }
        
        clearResidence()
        return true
//        Residence.getInstance().residenceManager.removeResidence(claimedResidence)
        
    }
    
    fun give(sender: Player, memberName: String): Boolean {
        if (memberName == owner) {
            return false
        }
        
        if (hasResidence()) {
            if (!com.bekvon.bukkit.residence.commands.give().perform(Residence.getInstance(), sender, arrayOf(residence, memberName, "-s", "-confirmed"), false)) {
                return false
            }
            
            if (!hasResidence()) {
                return false
            }
        }

        owner = memberName
        members.remove(memberName)
        members.add(sender.name)
        return true
    }
    
    fun clearResidence() {
        residence = ""
        Bukkit.getOnlinePlayers().forEach{
            if (isMember(it.name) || isOwner(it.name)) {
                it.sendMessage(MessageUtil.format(ConfigUtil.langConfig.autoRemoveRes))
            }
        }
    }
    
    fun hasResidence(): Boolean {
        if (residence.isEmpty()) {
            return false
        }

        val claimedResidence = Residence.getInstance().residenceManager.getByName(residence)
        
        if (claimedResidence == null) {
            clearResidence()
            return false
        }

        if (!claimedResidence.isOwner(owner)) {
            clearResidence()
            return false
        }
        return true
    }

    fun join(playerName: String): Boolean {
        if (donates.contains(playerName)) {
            return false
        }
        
        if (members.contains(playerName)) {
            return false
        }
        
        applies.remove(playerName)
        donates[playerName] = 0.0
        members.add(playerName)
        return true
    }
    
    fun apply(playerName: String): Boolean {
        if (applies.contains(playerName)) {
            return false
        }
        applies.add(playerName)
        return true
    }

    fun isOwner(playerName: String): Boolean {
        return this.owner == playerName
    }

    fun getPrefix(): String {
        return color.color + name
    }

    fun generateConfig(): YamlConfiguration {
        val config = YamlConfiguration()
        config.set("enabled", enabled)
        config.set("uuid", uuid)
        config.set("owner", owner)
        config.set("createTime", createTime.toString())
        
        val donatesSectionValue: ArrayList<String> = ArrayList()
        donates.forEach{
            val member = it.key + ":" + it.value
            donatesSectionValue.add(member)
        }
        members.forEach {
            if (!donates.contains(it)) {
                val member = "$it:0.0"
                donatesSectionValue.add(member)
            }
        }
        config.set("members", members.toList())
        config.set("donates", donatesSectionValue)

        val appliesSectionValue: ArrayList<String> = ArrayList()
        applies.forEach {
            appliesSectionValue.add(it)
        }
        config.set("applies", appliesSectionValue)
        config.set("name", name)
        config.set("residence", residence)
        config.set("color", color.toString())
        config.set("score", score)
        return config
    }
    //秒
//    private fun getPlayTimeMean():Double {
//        var result = 0.0
//        donates.keys.forEach{ result += Bukkit.getOfflinePlayer(it).getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 }
//        return result / donates.size
//    }
//
//    private fun getPlayTimeVariance(mean: Double):Double {
//        var result = 0.0
//        donates.keys.forEach {
//            result += (Bukkit.getOfflinePlayer(it).getStatistic(Statistic.PLAY_ONE_MINUTE) / 20 - mean)
//                .pow(2.0)
//        }
//
//        return result / donates.size
//    }

    fun getLevel() : Long{
//        val mean = getPlayTimeMean()
//        
//        val hours = mean / 60.0 / 60.0
//        val mins = getPlayTimeVariance(mean) / 3600.0
        var result = score / 1000
//        if(hours > 1) result *= hours
//        if(mins > 0.5) result /= mins

        return result.toLong()
    }

    companion object {
        fun generateInstance(config: ConfigurationSection): Guild? {
            if (!config.getBoolean("enabled", false)) {
                return null;
            }
            
            if (config.getString("owner", null) == null) {
                return null;
            }
            
            if (config.getString("name", null) == null) {
                return null;
            }

            return Guild(config)
        }
    }
}