package kr.kro.minestar.currency.data

import kr.kro.minestar.currency.exception.CurrencyException
import kr.kro.minestar.currency.function.ConfigClass
import kr.kro.minestar.currency.value.FolderValue
import kr.kro.minestar.utility.collection.toStringList
import kr.kro.minestar.utility.file.child
import kr.kro.minestar.utility.item.clearDisplay
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.material.item
import kr.kro.minestar.utility.string.remove
import org.bukkit.Material
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.inventory.ItemStack
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

        fun currencySet() = set.toSet()
        fun currencyUnitList() = set.toStringList()

        fun loadCurrencies() {
            val fileList = getCurrencyFolder().listFiles()

            if (fileList == null || fileList.isEmpty()) {
                val unit = ConfigClass().mainCurrencyUnit ?: "GOLD"
                Currency(unit)
                return
            }

            for (file in fileList) {
                if (file.isDirectory) continue
                if (!file.isFile) continue
                if (!file.name.contains(".yml")) return

                Currency(file)
            }
        }

        fun getCurrencyFolder() = FolderValue.currencyFolder()
    }

    //화폐단위
    val unit: String

    //화폐 아이콘
    private var icon: ItemStack
    fun icon() = icon.display(unit)
    internal fun icon(item: ItemStack) {
        item.clearDisplay()
        val yaml = getCurrencyYaml()
        yaml["icon"] = icon
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
        this.icon = Material.GOLD_INGOT.item()

        val yaml = getCurrencyYaml()
        yaml["unit"] = unit
        yaml["canPay"] = false
        yaml["canSend"] = false
        yaml["icon"] = icon

        yaml.save()
        registerCurrency(this)
    }

    constructor(yamlFile: File) {
        if (!yamlFile.exists()) throw CurrencyException("'${yamlFile.name}' file does not exist.")
        if (!yamlFile.name.contains(".yml")) throw CurrencyException("The file is not a YamlFile.")

        val yaml = YamlConfiguration.loadConfiguration(yamlFile)

        val unit = yaml.getString("unit") ?: throw CurrencyException("No unit or invalid class type.")
        if (yamlFile.name.remove(".yml") != unit) throw CurrencyException("The file name is not the same as the unit.")
        if (contains(unit)) throw CurrencyException("Currency of the same unit already exists.")
        this.unit = unit

        this.icon = yaml.getItemStack("icon") ?: Material.GOLD_INGOT.item()
        registerCurrency(this)
    }

    private fun getCurrencyFile() = getCurrencyFolder().child("$unit.yml")

    private fun getCurrencyYaml() = YamlConfiguration.loadConfiguration(getCurrencyFile())
    private fun YamlConfiguration.save() = save(getCurrencyFile())

    override fun toString() = unit
}