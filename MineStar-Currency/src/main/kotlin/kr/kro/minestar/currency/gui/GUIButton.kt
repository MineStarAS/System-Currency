package kr.kro.minestar.currency.gui

import kr.kro.minestar.currency.Main.Companion.head
import kr.kro.minestar.utility.item.Slot
import kr.kro.minestar.utility.item.display
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

enum class GUIButton(override val line: Int, override val number: Int, override val item: ItemStack) : Slot {
    PREVIOUS_PAGE(5, 0, head.item(8902, Material.BLUE_CONCRETE).display("§9[이전 페이지]")),
    NEXT_PAGE(5, 4, head.item(11504, Material.BLUE_CONCRETE).display("§7[현재 페이지]")),
    PAGE_NUMBER(5, 8, head.item(8899, Material.GRAY_CONCRETE).display("§9[이전 페이지]")),
    ;
}