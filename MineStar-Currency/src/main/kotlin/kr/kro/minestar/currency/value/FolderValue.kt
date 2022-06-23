package kr.kro.minestar.currency.value

import kr.kro.minestar.currency.function.ConfigClass
import org.bukkit.entity.Player
import java.io.File

object FolderValue {
    private fun dataFolder() = ConfigClass().dataSaveFolder

    private val playerFolder = File(dataFolder(), "players").apply { if (!exists()) mkdir() }
    fun playerFolder(player: Player) = File(playerFolder, "${player.uniqueId}").apply { if (!exists()) mkdir() }

    fun currencyFolder() = File(dataFolder(), "currencies").apply { if (!exists()) mkdir() }

    fun totalLogFolder() = File(dataFolder(), "totalLog").apply { if (!exists()) mkdir() }
}