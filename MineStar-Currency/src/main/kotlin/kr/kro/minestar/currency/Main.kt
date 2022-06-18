package kr.kro.minestar.currency

import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.currency.data.PlayerPurse
import kr.kro.minestar.currency.function.event.Event
import kr.kro.minestar.utility.item.Head
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    companion object {
        lateinit var pl: Main
        const val prefix = "§f[§9Currency§f]"
        lateinit var head: Head
    }

    override fun onEnable() {
        pl = this
        head = Head(pl)
        getCommand("currency")?.setExecutor(Command)

        Event

        Currency.loadCurrencies()

        for (player in Bukkit.getOnlinePlayers()) PlayerPurse(player)
    }

    override fun onDisable() {
        for (player in Bukkit.getOnlinePlayers()) try {
            player.closeInventory()
        } catch (_: Exception) {
        }
    }
}