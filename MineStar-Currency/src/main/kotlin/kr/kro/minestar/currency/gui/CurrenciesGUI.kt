package kr.kro.minestar.currency.gui

import kr.kro.minestar.currency.Main
import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.currency.data.PlayerPurse
import kr.kro.minestar.currency.function.ConfigClass
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import kr.kro.minestar.utility.item.addPrefix
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.number.addComma
import kr.kro.minestar.utility.string.unColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class CurrenciesGUI(override val player: Player) : GUI() {

    private fun currencies(): Set<Currency> {
        val set = hashSetOf<Currency>()
        val mainCurrency = ConfigClass().mainCurrency
        if (mainCurrency != null) set.add(mainCurrency)
        for (currency in Currency.currencySet()) set.add(currency)
        return set.toSet()
    }

    private fun guiLineAmount() = currencies().size / 9 + 1

    private val playerPurse = PlayerPurse.getPlayerPurse(player)
    override val gui = InventoryUtil.gui(guiLineAmount(), "${player.name}의 지갑")
    override val pl = Main.pl

    /**
     * function
     */
    override fun displaying() {
        gui.clear()

        for ((slot, currency) in currencies().withIndex()) {
            gui.setItem(slot, currency.icon().addPrefix(playerPurse?.currencyAmount(currency)?.addComma() ?: "0"))
        }
    }

    @EventHandler
    override fun clickGUI(e: InventoryClickEvent) {
        if (e.whoClicked != player) return
        if (e.inventory != gui) return
        e.isCancelled = true

        if (e.clickedInventory != e.view.topInventory) return
        if (e.click != ClickType.LEFT) return

        val clickItem = e.currentItem ?: return

        val currency = Currency.getCurrency(clickItem.display().unColor()) ?: return
        CurrencyGUI(player, currency)
    }

    init {
        openGUI()
    }
}