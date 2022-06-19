package kr.kro.minestar.currency.gui

import kr.kro.minestar.currency.Main
import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.currency.data.PlayerPurse
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import kr.kro.minestar.utility.item.*
import kr.kro.minestar.utility.material.item
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class PlayerCurrencyLogGUI(override val player: Player, private val currency: Currency) : GUI() {

    private enum class Button(override val line: Int, override val number: Int, override val item: ItemStack) : Slot {
        PREVIOUS_PAGE(5, 0, Main.head.item(8902, Material.BLUE_CONCRETE).display("§9[이전 페이지]")),
        NEXT_PAGE(5, 8, Main.head.item(11504, Material.BLUE_CONCRETE).display("§7[다음 페이지]")),
        PAGE_NUMBER(5, 4, Main.head.item(8899, Material.GRAY_CONCRETE).display("§9[현재 페이지]")),
        ;
    }

    override val pl = Main.pl
    override val gui = InventoryUtil.gui(6, "${player.name} $currency 기록")

    /**
     * Page function
     */
    private fun dayKeys(): Set<String> {

        val playerPurse = PlayerPurse.getPlayerPurse(player) ?: return setOf()
        val keys = playerPurse.getCurrencyLogYaml(currency).getKeys(false)

        keys.remove("amount")

        return keys.toSet()
    }

    private fun timeKeys(dayKey: String): Set<String> {
        val timeKeys = mutableListOf<String>()
        val playerPurse = PlayerPurse.getPlayerPurse(player) ?: return setOf()
        val keys = playerPurse.getCurrencyLogYaml(currency).getKeys(true)

        for (key in keys) if (key.contains(dayKey)) if (key.split('.').size == 3) timeKeys.add(key)

        return timeKeys.toSet()
    }

    private var page = 0

    private fun pageSection(): Pair<Int, Int> {
        val first = page * 45
        val second = first + 44

        return Pair(first, second)
    }

    private fun isOverNumber() = dayKeys().size > pageSection().first

    /**
     * function
     */
    override fun displaying() {
        gui.clear()
        setItems(Button.values())

        val logItem = Material.MOJANG_BANNER_PATTERN.item().flagAll()

        val pageSection = pageSection()
        val dayKeys = dayKeys().toList()
        if (dayKeys.isEmpty()) return

        if (page < 64) {
            val slot = Button.PAGE_NUMBER
            val pageNumberItem = slot.item.clone().amount(page + 1)
            gui.setItem(slot.getIndex(), pageNumberItem)
        }

        for ((slot, index) in (pageSection.first..pageSection.second).withIndex()) {
            if (dayKeys.size <= index) break
            val item = logItem.clone()
            val dayKey = dayKeys[index]
            item.display(dayKey)

            val timeKeys = timeKeys(dayKey)

            val playerPurse = PlayerPurse.getPlayerPurse(player) ?: return
            val yaml = playerPurse.getCurrencyLogYaml(currency)

            for (key in timeKeys) {
                val log = yaml.getString(key) ?: continue
                item.addLore("[${key.split('.')[1]}]")
                item.addLore(log)
            }

            gui.setItem(slot, item)
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

        when (getSlot(clickItem, Button.values())) {
            Button.NEXT_PAGE -> {
                page++
                if (isOverNumber()) {
                    page--
                    return
                }
                displaying()
            }
            Button.PREVIOUS_PAGE -> {
                page--
                if (page < 0) {
                    page++
                    return
                }
                displaying()
            }

            Button.PAGE_NUMBER -> {}

            null -> {
                clickItem.display().toPlayer(player)

                val loreList = clickItem.lore ?: return
                for (lore in loreList) {
                    lore ?: continue
                    lore.toPlayer(player)
                }
            }
        }
    }
    init {
        openGUI()
    }
}