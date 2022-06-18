package kr.kro.minestar.currency.gui

import kr.kro.minestar.currency.Main
import kr.kro.minestar.currency.Main.Companion.prefix
import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.currency.data.PlayerPurse
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import kr.kro.minestar.utility.item.Slot
import kr.kro.minestar.utility.item.addLore
import kr.kro.minestar.utility.item.clearLore
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.number.addComma
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class CurrencyGUI(override val player: Player, val currency: Currency) : GUI() {

    private enum class Button(override val line: Int, override val number: Int, override val item: ItemStack) : Slot {
        CURRENCY_AMOUNT(0, 4, Main.head.item(8902, Material.BLUE_CONCRETE).display("§6[보유 금액]")),
        SEND_AMOUNT(0, 5, Main.head.item(40473, Material.BLUE_CONCRETE).display("§a[송금하기]")),
        ;
    }

    override val gui = InventoryUtil.gui(1, "$currency 정보")
    override val pl = Main.pl

    private val playerPurse = PlayerPurse.getPlayerPurse(player)

    private fun hasAmount() = this.playerPurse?.currencyAmount(currency) ?: 0

    /**
     * function
     */
    override fun displaying() {
        gui.clear()
        Button.CURRENCY_AMOUNT.item.clearLore().addLore("§e${hasAmount().addComma()} ${currency.unit}")
        setItems(Button.values())
    }

    @EventHandler
    override fun clickGUI(e: InventoryClickEvent) {
        if (e.whoClicked != player) return
        if (e.inventory != gui) return
        e.isCancelled = true

        if (e.clickedInventory != e.view.topInventory) return
        if (e.click != ClickType.LEFT) return

        val clickItem = e.currentItem ?: return

        val slot = getSlot(clickItem, Button.values()) ?: return

        when (slot) {
            Button.CURRENCY_AMOUNT -> {}
            Button.SEND_AMOUNT -> {
                if (!currency.canSend()) return "$prefix §c송금 할 수 없는 화폐입니다.".toPlayer(player)
                PlayersGUI(player, currency, javaClass, CalculatorGUI::class.java)
            }
        }
    }

    init {
        openGUI()
    }
}