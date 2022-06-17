package kr.kro.minestar.currency.data

import kr.kro.minestar.currency.exception.CurrencyException
import kr.kro.minestar.currency.value.FolderValue
import kr.kro.minestar.utility.collection.toStringList
import kr.kro.minestar.utility.string.remove
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File

class Currency {

    companion object {
        private val set = hashSetOf<Currency>()
        private fun registerCurrency(currency: Currency) = set.add(currency)
        private fun unregisterCurrency(currency: Currency) = set.remove(currency)

        fun getCurrency(unit: String): Currency? {
            for (currency in set) if (currency.unit == unit) return currency
            return null
        }

        fun contains(unit: String): Boolean {
            for (currency in set) if (currency.unit == unit) return true
            return false
        }

        fun currencyUnitList() = set.toStringList()
    }

    //화폐단위
    val unit: String

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

        val yaml = getCurrencyYaml()
        yaml["canPay"] = false
        yaml["canSend"] = false
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
    }


    private fun getCurrencyFile() = File(FolderValue.currencyFolder, "$unit.yml")

    private fun getCurrencyYaml() = YamlConfiguration.loadConfiguration(getCurrencyFile())
    private fun YamlConfiguration.save() = save(getCurrencyFile())

    override fun toString() = unit
}