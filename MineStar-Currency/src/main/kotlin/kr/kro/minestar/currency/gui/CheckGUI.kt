package kr.kro.minestar.currency.gui

import kr.kro.minestar.currency.Main
import kr.kro.minestar.currency.Main.Companion.head
import kr.kro.minestar.currency.Main.Companion.prefix
import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.currency.data.PlayerPurse
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import kr.kro.minestar.utility.item.Slot
import kr.kro.minestar.utility.item.addLore
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.number.addComma
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class CheckGUI(
    override val player: Player, private val targetPlayer: Player,
    private val currency: Currency, private val processAmount: Long, private val processType: ProcessType,
) : GUI() {

    private enum class Button(override val line: Int, override val number: Int, override val item: ItemStack) : Slot {
        CANCEL(0, 2, head.item(9382, Material.RED_CONCRETE).display("§c[§f취소§c]")),
        ACCEPT(0, 6, head.item(21771, Material.LIME_CONCRETE).display("§a[§f확인§a]")),
        ;
    }

    override val pl = Main.pl
    override val gui = InventoryUtil.gui(1, "확인창")

    override fun displaying() {
        gui.clear()

        val processItem = when (processType) {
            ProcessType.SEND -> head.item(9382, Material.LIGHT_BLUE_CONCRETE)
            ProcessType.SET -> head.item(9382, Material.YELLOW_CONCRETE)
            ProcessType.ADD -> head.item(9382, Material.LIME_CONCRETE)
            ProcessType.REMOVE -> head.item(9382, Material.RED_CONCRETE)
            //TODO(머리 ID 수정 해야함)
        }
        processItem.display("§9[§f처리 형태§9] $processType")
            .addLore(" ")
            .addLore("§b[§f처리 액수§b]")
            .addLore("§f${processAmount.addComma()} §e$currency")
            .addLore(" ")
            .addLore("§b[§f처리 대상§b]")
            .addLore("§f${targetPlayer.name}")

        gui.setItem(4, processItem)

        setItems(Button.values())
    }

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
            Button.ACCEPT -> {
                when (processType) {
                    ProcessType.SEND -> {
                        val playerPurse = PlayerPurse.getPlayerPurse(player) ?: return "$prefix §c자신의 지갑이 불러올 수 없습니다.".toPlayer(player)
                        playerPurse.currencyAmountSand(currency, processAmount, targetPlayer, player.name)
                    }
                    ProcessType.SET -> {
                        val playerPurse = PlayerPurse.getPlayerPurse(targetPlayer) ?: return "$prefix §c대상의 지갑이 불러올 수 없습니다.".toPlayer(player)
                        playerPurse.currencyAmountSet(currency, processAmount, player.name)
                    }
                    ProcessType.ADD -> {
                        val playerPurse = PlayerPurse.getPlayerPurse(targetPlayer) ?: return "$prefix §c대상의 지갑이 불러올 수 없습니다.".toPlayer(player)
                        playerPurse.currencyAmountAdd(currency, processAmount, player.name)
                    }
                    ProcessType.REMOVE -> {
                        val playerPurse = PlayerPurse.getPlayerPurse(targetPlayer) ?: return "$prefix §c대상의 지갑이 불러올 수 없습니다.".toPlayer(player)
                        playerPurse.currencyAmountRemove(currency, processAmount, player.name)
                    }
                }
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