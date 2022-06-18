package kr.kro.minestar.currency.gui

import kr.kro.minestar.currency.Main
import kr.kro.minestar.currency.Main.Companion.head
import kr.kro.minestar.currency.Main.Companion.prefix
import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import kr.kro.minestar.utility.item.Slot
import kr.kro.minestar.utility.item.amount
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta

class PlayersGUI(
    override val player: Player, private val currency: Currency,
    private val backGUI: Class<out GUI>, private val nextGUI: Class<out GUI>
) : GUI() {

    private enum class Button(override val line: Int, override val number: Int, override val item: ItemStack) : Slot {
        PREVIOUS_PAGE(5, 0, head.item(8902, Material.BLUE_CONCRETE).display("§9[이전 페이지]")),
        NEXT_PAGE(5, 4, head.item(11504, Material.BLUE_CONCRETE).display("§7[현재 페이지]")),
        PAGE_NUMBER(5, 8, head.item(8899, Material.GRAY_CONCRETE).display("§9[이전 페이지]")),
        ;
    }

    override val gui = InventoryUtil.gui(6, "${player.name}의 지갑")
    override val pl = Main.pl

    private fun players() = Bukkit.getOnlinePlayers().toList()

    /**
     * Page function
     */
    private var page = 0

    private fun pageSection(): Pair<Int, Int> {
        val first = page * 45
        val second = first + 44

        return Pair(first, second)
    }

    private fun isOverNumber() = players().size > pageSection().first

    /**
     * function
     */
    override fun displaying() {
        gui.clear()
        setItems(Button.values())
        val pageSection = pageSection()
        val players = players()

        if (page < 64) {
            val slot = Button.PAGE_NUMBER
            val pageNumberItem = slot.item.clone().amount(page + 1)
            gui.setItem(slot.getIndex(), pageNumberItem)
        }

        for ((slot, index) in (pageSection.first..pageSection.second).withIndex()) {
            val player = players[index]
            gui.setItem(slot, head.item(player).display(player.name))
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

        val slot = getSlot(clickItem, Button.values())

        when (slot) {
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
                if (clickItem.type != Material.PLAYER_HEAD) return
                val meta = clickItem.itemMeta
                if (meta !is SkullMeta) return
                val playerUUID = meta.owningPlayer?.uniqueId ?: return
                val targetPlayer = Bukkit.getPlayer(playerUUID) ?: return

                if (!targetPlayer.isOnline) return "$prefix §c해당 플레이어는 오프라인 상태 입니다.".toPlayer(player)

                when (Pair(backGUI, nextGUI)) {
                    Pair(CurrencyGUI::class.java, CalculatorGUI::class.java) -> CalculatorGUI(player, targetPlayer, currency, CheckSendCurrencyGUI::class.java)
                }
            }
        }

    }

    init {
        openGUI()
    }
}