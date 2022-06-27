package kr.kro.minestar.currency.function

import kr.kro.minestar.currency.Main.Companion.plugin
import kr.kro.minestar.currency.data.Currency
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File


class ConfigClass {
    private val file = File(plugin.dataFolder, "resources/config.yml").apply {
        if (!exists()) plugin.saveResource("resources/config.yml", false)
    }

    private val config = YamlConfiguration.loadConfiguration(file)

    val mainCurrencyUnit = config.getString("mainCurrency")
    val mainCurrency = Currency.getCurrency(mainCurrencyUnit)

    val dataSaveFolder = when (config.getString("dataSaveFolder")) {
        null,
        "null",
        "default",
        -> plugin.dataFolder
        else -> File(config.getString("dataSaveFolder")!!, "currency")
    }

    val simplePermission = config.getBoolean("simplePermission")
}