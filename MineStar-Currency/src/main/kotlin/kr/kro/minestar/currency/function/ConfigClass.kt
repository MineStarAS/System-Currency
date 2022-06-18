package kr.kro.minestar.currency.function

import kr.kro.minestar.currency.Main.Companion.pl
import kr.kro.minestar.currency.data.Currency
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File


class ConfigClass {
    private val file = File(pl.dataFolder, "config.yml").apply {
        if (!exists()) pl.saveResource("config.yml", false)
    }

    private val config = YamlConfiguration.loadConfiguration(file)

    val mainCurrency = Currency.getCurrency(config.getString("mainCurrency"))
    val dataSaveFolder = when (config.getString("dataSaveFolder")) {
        null,
        "null",
        "default",
        -> pl.dataFolder
        else -> File(config.getString("dataSaveFolder")!!, "currency")
    }
}