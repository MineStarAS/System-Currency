package kr.kro.minestar.currency.gui

import kr.kro.minestar.currency.Main
import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.currency.data.PlayerPurse
import kr.kro.minestar.currency.function.ConfigClass
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import kr.kro.minestar.utility.item.addLore
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.number.addComma
import kr.kro.minestar.utility.string.remove
import kr.kro.minestar.utility.string.script
import kr.kro.minestar.utility.string.toPlayer
import kr.kro.minestar.utility.string.unColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

class PlayerPurseGUI(override val player: Player) : GUI() {

    private fun currencies(): Set<Currency> {
        val set = hashSetOf<Currency>()
        val mainCurrency = ConfigClass().mainCurrency
        if (mainCurrency != null) set.add(mainCurrency)
        for (currency in Currency.currencySet()) set.add(currency)
        return set.toSet()
    }

    private fun guiLineAmount() = currencies().size / 9 + 1

    private val playerPurse = PlayerPurse.getPlayerPurse(player)
    override val gui = InventoryUtil.gui(guiLineAmount(), "화폐 목록")
    override val plugin = Main.plugin

    /**
     * function
     */
    override fun displaying() {
        gui.clear()

        for ((slot, currency) in currencies().withIndex()) {
            val item = currency.icon()
            val amount = playerPurse?.currencyAmount(currency) ?: 0

            item.display("§e[ §f$currency §e]")
            item.addLore(" ")
            item.addLore("§6[§f보유금액§6] §f${amount.addComma()} §e$currency")
            item.addLore(" ")
            item.addLore("§7[좌클릭] 송금하기")
            item.addLore("§7[쉬프트 좌클릭] 기록보기")

            gui.setItem(slot, item)
        }
    }

    @EventHandler
    override fun clickGUI(e: InventoryClickEvent) {
        if (e.whoClicked != player) return
        if (e.inventory != gui) return
        e.isCancelled = true

        if (e.clickedInventory != e.view.topInventory) return

        val clickItem = e.currentItem ?: return
        val display = clickItem.display().unColor().remove("[ ").remove(" ]")

        val currency = Currency.getCurrency(display) ?: return

        when(e.click) {
            ClickType.LEFT -> {
                if (!currency.canSend()) return "§c송금 할 수 없는 화폐입니다.".script(plugin.prefix).toPlayer(player)
                PlayersGUI(player, currency, javaClass)
            }
            ClickType.SHIFT_LEFT -> PlayerCurrencyLogListGUI(player, currency)

            else -> return
        }
    }

    init {
        openGUI()
    }
}