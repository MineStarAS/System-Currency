package kr.kro.minestar.currency.gui

import kr.kro.minestar.currency.Main
import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.currency.data.PlayerPurse
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.string.unColor
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class PlayerPurseGUI(val playerPurse: PlayerPurse) : GUI(playerPurse.player) {
    private fun currencies() = Currency.currencyList()
    private fun guiLineAmount() = currencies().size / 9 + 1

    override val gui = InventoryUtil.gui(guiLineAmount(), "${player.name}의 지갑")
    override val pl = Main.pl

    private var currentCurrency: Currency? = null
    fun currentCurrency() = currentCurrency

    private var targetPlayer: Player? = null
    fun targetPlayer() = targetPlayer

    override fun displaying() {
        gui.clear()

        for ((slot, currency) in currencies().withIndex()) gui.setItem(slot, currency.icon())
    }

    override fun clickGUI(e: InventoryClickEvent) {
        if (e.whoClicked != player) return
        if (e.inventory != gui) return
        e.isCancelled = true

        if (e.clickedInventory != e.view.topInventory) return

        val clickItem = e.currentItem ?: return

        val currency = Currency.getCurrency(clickItem.display().unColor()) ?: return
        currentCurrency = currency
        CurrencyGUI(currentCurrency!!, this)
    }
}