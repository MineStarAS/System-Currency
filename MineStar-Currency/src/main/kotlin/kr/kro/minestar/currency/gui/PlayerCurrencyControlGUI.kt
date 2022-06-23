package kr.kro.minestar.currency.gui

import kr.kro.minestar.currency.Main
import kr.kro.minestar.currency.Main.Companion.head
import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.currency.data.PlayerPurse
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import kr.kro.minestar.utility.item.Slot
import kr.kro.minestar.utility.item.addLore
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.number.addComma
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class PlayerCurrencyControlGUI(
    override val player: Player, private val targetPlayer: Player, private val currency: Currency,
) : GUI() {
    private enum class Button(override val line: Int, override val number: Int, override val item: ItemStack) : Slot {
        SET(0, 4, head.item(9171, Material.YELLOW_CONCRETE).display("§e[§f금액 설정§e]")),
        ADD(0, 5, head.item(9885, Material.LIME_CONCRETE).display("§a[§f금액 추가§a]")),
        REMOVE(0, 6, head.item(9351, Material.RED_CONCRETE).display("§c[§f금액 감가§c]")),
        ;
    }

    override val pl = Main.pl
    override val gui = InventoryUtil.gui(1, "${targetPlayer.name} $currency 제어판")

    override fun displaying() {
        gui.clear()

        val playerItem = head.item(targetPlayer)
            .display("§a[§f플레이어§a] §f${targetPlayer.name}")
            .addLore("§7[§fUUID§7] §f${targetPlayer.uniqueId}")

        gui.setItem(0, playerItem)

        val currencyItem = currency.icon()
        val amount = PlayerPurse.getPlayerPurse(targetPlayer)?.currencyAmount(currency) ?: 0

        currencyItem.display("§e[ §f$currency §e]")
        currencyItem.addLore(" ")
        currencyItem.addLore("§6[§f보유 금액§6] §f${amount.addComma()} §e$currency")

        gui.setItem(2, currencyItem)

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

        val processType = when (slot) {
            Button.SET -> ProcessType.SET
            Button.ADD -> ProcessType.ADD
            Button.REMOVE -> ProcessType.REMOVE
            else -> return
        }

        CalculatorGUI(player, targetPlayer, currency, processType)
    }

    init {
        openGUI()
    }
}