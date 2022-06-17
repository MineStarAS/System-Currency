package kr.kro.minestar.currency.gui

import kr.kro.minestar.currency.Main
import kr.kro.minestar.currency.data.PlayerPurse
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import org.bukkit.event.inventory.InventoryClickEvent

class PlayerPurseGUI(val playerPurse: PlayerPurse) : GUI(playerPurse.player) {
    override val gui = InventoryUtil.gui(6, "${player.name}의 지갑")
    override val pl = Main.pl

    override fun displaying() {
        TODO("Not yet implemented")
    }

    override fun clickGUI(e: InventoryClickEvent) {
        TODO("Not yet implemented")
    }

}