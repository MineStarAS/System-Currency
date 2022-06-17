package kr.kro.minestar.currency

import kr.kro.minestar.currency.function.event.Event
import kr.kro.minestar.utility.item.Head
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

        println(javaClass.packageName)
    }

    override fun onDisable() {
    }
}