package kr.kro.minestar.currency.gui

import kr.kro.minestar.currency.Main
import kr.kro.minestar.currency.Main.Companion.prefix
import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.currency.data.PlayerPurse
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import kr.kro.minestar.utility.item.Slot
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class CheckSendCurrencyGUI(
    override val player: Player, val targetPlayer: Player,
    private val currency: Currency, private val sandAmount: Long
) : GUI() {

    private enum class Button(override val line: Int, override val number: Int, override val item: ItemStack) : Slot {
        CANCEL(0, 2, Main.head.item(9382, Material.RED_CONCRETE).display("§c[취소]")),
        SEND(0, 6, Main.head.item(21771, Material.LIME_CONCRETE).display("§a[송금]")),
        ;
    }

    override val pl = Main.pl
    override val gui = InventoryUtil.gui(1, "송금 확인")

    @EventHandler
    override fun clickGUI(e: InventoryClickEvent) {
        if (e.whoClicked != player) return
        if (e.inventory != gui) return
        e.isCancelled = true

        val clickItem = e.currentItem ?: return

        if (e.clickedInventory != e.view.topInventory) return
        if (e.click != ClickType.LEFT) return

        val slot = getSlot(clickItem, Button.values()) ?: return
        when (slot) {
            Button.SEND -> {
                val playerPurse = PlayerPurse.getPlayerPurse(player) ?: return "$prefix §c자신의 지갑이 불러올 수 없습니다.".toPlayer(player)
                playerPurse.currencyAmountSand(currency, sandAmount, targetPlayer, player.name)
            }
            Button.CANCEL -> "$prefix §c송금을 취소 하였습니다.".toPlayer(player)
        }

        try {
            player.closeInventory()
        } catch (_: Exception) {
        }
    }

    init {
        openGUI()
    }
}