package kr.kro.minestar.currency.data

import kr.kro.minestar.currency.exception.CurrencyException
import kr.kro.minestar.currency.value.FolderValue
import kr.kro.minestar.utility.collection.toStringList
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.string.remove
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class Currency {

    companion object {
        private val set = hashSetOf<Currency>()
        private fun registerCurrency(currency: Currency) = set.add(currency)
        private fun unregisterCurrency(currency: Currency) = set.remove(currency)

        fun getCurrency(unit: String?): Currency? {
            unit ?: return null
            for (currency in set) if (currency.unit == unit) return currency
            return null
        }

        fun contains(unit: String?): Boolean {
            unit ?: return false
            for (currency in set) if (currency.unit == unit) return true
            return false
        }

        fun currencyList() = set.toList()
        fun currencyUnitList() = set.toStringList()
    }

    //화폐단위
    val unit: String

    //화폐 아이콘
    private var icon: Icon
    fun icon() = icon.item().display(unit)
    internal fun icon(icon: Icon) {
        val yaml = getCurrencyYaml()
        yaml["icon.material"] = icon.material.name
        yaml["icon.customModelData"] = icon.customModelData
        yaml.save()
    }

    /**
     * Restriction // 제한여부
     */
    //송금
    fun canSend() = getCurrencyYaml().getBoolean("canSend")
    internal fun canSend(boolean: Boolean) {
        val yaml = getCurrencyYaml()
        yaml["canSend"] = boolean
        yaml.save()
    }

    //지불
    fun canPay() = getCurrencyYaml().getBoolean("canPay")
    internal fun canPay(boolean: Boolean) {
        val yaml = getCurrencyYaml()
        yaml["canPay"] = boolean
        yaml.save()
    }

    /**
     * constructor // 생성자
     */
    constructor(unit: String) {
        this.unit = unit
        this.icon = Icon(Material.GOLD_INGOT, null)

        val yaml = getCurrencyYaml()
        yaml["canPay"] = false
        yaml["canSend"] = false
        yaml["icon.material"] = icon.material.name
        yaml["icon.customModelData"] = icon.customModelData

        yaml.save()
        registerCurrency(this)
    }

    constructor(yamlFile: File) {
        if (yamlFile.exists()) throw CurrencyException("The file does not exist.")
        if (!yamlFile.name.contains(".yml")) throw CurrencyException("The file is not a YamlFile.")

        val yaml = YamlConfiguration.loadConfiguration(yamlFile)

        val unit = yaml.getString("unit") ?: throw CurrencyException("No unit or invalid class type.")
        if (yamlFile.name.remove(".yml") != unit) throw CurrencyException("The file name is not the same as the unit.")
        if (contains(unit)) throw CurrencyException("Currency of the same unit already exists.")
        this.unit = unit

        val iconMaterial = Material.getMaterial(yaml.getString("icon.material") ?: Material.GOLD_INGOT.name) ?: Material.GOLD_INGOT
        var iconCustomModelData : Int? = yaml.getInt("icon.customModelData")
        if (iconCustomModelData == 0) iconCustomModelData = null

        this.icon = Icon(iconMaterial, iconCustomModelData)
    }


    private fun getCurrencyFile() = File(FolderValue.currencyFolder, "$unit.yml")

    private fun getCurrencyYaml() = YamlConfiguration.loadConfiguration(getCurrencyFile())
    private fun YamlConfiguration.save() = save(getCurrencyFile())

    override fun toString() = unit
}