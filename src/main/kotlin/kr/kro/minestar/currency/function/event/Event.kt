package kr.kro.minestar.currency.function.event

import kr.kro.minestar.currency.Main.Companion.plugin
import kr.kro.minestar.currency.data.PlayerPurse
import kr.kro.minestar.utility.event.enable
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

object Event : Listener {
    init {
        enable(plugin)
    }

    @EventHandler
    fun join(e: PlayerJoinEvent) {
        val player = e.player
        if (PlayerPurse.contains(player)) return
        PlayerPurse(player)
    }
}