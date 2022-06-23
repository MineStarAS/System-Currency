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

    override val plugin = Main.plugin
    override val gui = InventoryUtil.gui(1, "확인창")
    private val prefix = plugin.prefix

    override fun displaying() {
        gui.clear()

        val processItem = when (processType) {
            ProcessType.SEND -> head.item(10143, Material.LIGHT_BLUE_CONCRETE)
            ProcessType.SET -> head.item(9171, Material.YELLOW_CONCRETE)
            ProcessType.ADD -> head.item(9885, Material.LIME_CONCRETE)
            ProcessType.REMOVE -> head.item(9351, Material.RED_CONCRETE)
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
                        val booleanScript = playerPurse.currencyAmountSend(currency, processAmount, targetPlayer, player.name)
                        if (!booleanScript.boolean) "$prefix §c${booleanScript.script}".toPlayer(player)
                    }
                    ProcessType.SET -> {
                        val playerPurse = PlayerPurse.getPlayerPurse(targetPlayer) ?: return "$prefix §c대상의 지갑이 불러올 수 없습니다.".toPlayer(player)
                        playerPurse.currencyAmountSet(currency, processAmount, player.name)

                        "$prefix §e${targetPlayer.name} §f님의 보유금액을 §e${processAmount.addComma()} §6$currency §f으/로 §e설정 §f하였습니다.".toPlayer(player)
                    }
                    ProcessType.ADD -> {
                        val playerPurse = PlayerPurse.getPlayerPurse(targetPlayer) ?: return "$prefix §c대상의 지갑이 불러올 수 없습니다.".toPlayer(player)
                        playerPurse.currencyAmountAdd(currency, processAmount, player.name)

                        "$prefix §e${targetPlayer.name} §f님에게 §e${processAmount.addComma()} §6$currency §f을/를 §a추가 §f하였습니다.".toPlayer(player)
                    }
                    ProcessType.REMOVE -> {
                        val playerPurse = PlayerPurse.getPlayerPurse(targetPlayer) ?: return "$prefix §c대상의 지갑이 불러올 수 없습니다.".toPlayer(player)
                        playerPurse.currencyAmountRemove(currency, processAmount, player.name)

                        "$prefix §e${targetPlayer.name} §f님에게 §e${processAmount.addComma()} §6$currency §f을/를 §c감가 §f하였습니다.".toPlayer(player)
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