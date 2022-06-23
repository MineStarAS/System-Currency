package kr.kro.minestar.currency

import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.currency.data.PlayerPurse
import kr.kro.minestar.currency.function.event.Event
import kr.kro.minestar.utility.item.Head
import kr.kro.minestar.utility.main.FunctionalJavaPlugin
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : FunctionalJavaPlugin() {
    companion object {
        lateinit var head: Head
        private lateinit var privatePlugin: FunctionalJavaPlugin
        val plugin = privatePlugin
    }

    override fun onEnable() {
        privatePlugin = this
        privatePrefix = "§9Currency"
        head = Head(this)
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