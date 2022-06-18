package kr.kro.minestar.currency.value

import kr.kro.minestar.currency.Main.Companion.pl
import kr.kro.minestar.currency.function.ConfigClass
import org.bukkit.entity.Player
import java.io.File

object FolderValue {
    private fun dataFolder() = ConfigClass().dataSaveFolder

    private val playerFolder = File(dataFolder(), "player")
    fun playerFolder (player: Player) = File(playerFolder, "${player.uniqueId}")

    val currencyFolder = File(dataFolder(), "currency")

    val totalLogFolder = File(dataFolder(), "totalLog")
}