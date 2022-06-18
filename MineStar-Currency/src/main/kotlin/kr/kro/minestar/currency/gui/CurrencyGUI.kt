package kr.kro.minestar.currency.gui

import kr.kro.minestar.currency.Main
import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.currency.data.PlayerPurse
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import kr.kro.minestar.utility.item.addLore
import kr.kro.minestar.utility.item.amount
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.number.addComma
import kr.kro.minestar.utility.string.toServer
import kr.kro.minestar.utility.string.unColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.SkullMeta

class CurrencyGUI(val currency: Currency, val playerPurseGUI: PlayerPurseGUI) : GUI(playerPurseGUI.playerPurse.player) {
    override val gui = InventoryUtil.gui(1, "$currency 정보")
    override val pl = Main.pl

    private val purse = playerPurseGUI.playerPurse

    private fun hasAmount() = purse.currencyAmount(currency)

    override fun displaying() {
        gui.clear()
        val currencyIcon = currency.icon().display("§6[보유 금액]").addLore("§e${hasAmount().addComma()} ${currency.unit}")
    }

    override fun clickGUI(e: InventoryClickEvent) {
        if (e.whoClicked != player) return
        if (e.inventory != gui) return
        e.isCancelled = true

        if (e.clickedInventory != e.view.topInventory) return

        val clickItem = e.currentItem ?: return

        val currency = Currency.getCurrency(clickItem.display().unColor()) ?: return
    }

}