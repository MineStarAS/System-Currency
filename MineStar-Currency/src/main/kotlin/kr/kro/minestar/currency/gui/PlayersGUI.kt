package kr.kro.minestar.currency.gui

import kr.kro.minestar.currency.Main
import kr.kro.minestar.currency.Main.Companion.head
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import kr.kro.minestar.utility.item.amount
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.string.toServer
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.meta.SkullMeta

class PlayersGUI(player: Player) : GUI(player) {
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

    override fun displaying() {
        gui.clear()
        setItems(gui, GUIButton.values())
        val pageSection = pageSection()
        val players = players()

        if (page < 64) {
            val slot = GUIButton.PAGE_NUMBER
            val pageNumberItem = slot.item.clone().amount(page + 1)
            gui.setItem(slot.getIndex(), pageNumberItem)
        }

        for ((slot, index) in (pageSection.first..pageSection.second).withIndex()) {
            val player = players[index]
            gui.setItem(slot, head.item(player).display(player.name))
        }
    }

    override fun clickGUI(e: InventoryClickEvent) {
        if (e.whoClicked != player) return
        if (e.inventory != gui) return
        e.isCancelled = true

        if (e.clickedInventory != e.view.topInventory) return

        val clickItem = e.currentItem ?: return

        val slot = getSlot(clickItem, GUIButton.values())

        when (slot) {
            GUIButton.NEXT_PAGE -> {
                page++
                if (isOverNumber()) {
                    page--
                    return
                }
                displaying()
            }
            GUIButton.PREVIOUS_PAGE -> {
                page--
                if (page < 0) {
                    page++
                    return
                }
                displaying()
            }

            GUIButton.PAGE_NUMBER -> {}

            null -> {
                if (clickItem.type != Material.PLAYER_HEAD) return
                val meta = clickItem.itemMeta
                if (meta !is SkullMeta) return
                val playerUUID = meta.owningPlayer?.uniqueId ?: return
                val targetPlayer = Bukkit.getPlayer(playerUUID) ?: return

                targetPlayer.name.toServer()
            }
        }

    }

}