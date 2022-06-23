package kr.kro.minestar.currency.gui

import kr.kro.minestar.currency.Main
import kr.kro.minestar.currency.Main.Companion.head
import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.utility.gui.GUI
import kr.kro.minestar.utility.inventory.InventoryUtil
import kr.kro.minestar.utility.item.Slot
import kr.kro.minestar.utility.item.display
import kr.kro.minestar.utility.number.addComma
import kr.kro.minestar.utility.string.unColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class CalculatorGUI(
    override val player: Player, private val targetPlayer: Player,
    private val currency: Currency, private val processType: ProcessType
) : GUI() {

    private enum class DisplayNumber(override val line: Int, override val number: Int, override val item: ItemStack) : Slot {
        NUMBER_0(0, 0, head.item(9271, Material.WHITE_CONCRETE).display("0")),
        NUMBER_1(0, 0, head.item(9270, Material.WHITE_CONCRETE).display("1")),
        NUMBER_2(0, 0, head.item(9269, Material.WHITE_CONCRETE).display("2")),
        NUMBER_3(0, 0, head.item(9268, Material.WHITE_CONCRETE).display("3")),
        NUMBER_4(0, 0, head.item(9267, Material.WHITE_CONCRETE).display("4")),
        NUMBER_5(0, 0, head.item(9266, Material.WHITE_CONCRETE).display("5")),
        NUMBER_6(0, 0, head.item(9265, Material.WHITE_CONCRETE).display("6")),
        NUMBER_7(0, 0, head.item(9264, Material.WHITE_CONCRETE).display("7")),
        NUMBER_8(0, 0, head.item(9263, Material.WHITE_CONCRETE).display("8")),
        NUMBER_9(0, 0, head.item(9262, Material.WHITE_CONCRETE).display("9")),
        NUMBER_NULL(0, 0, head.item(9243, Material.WHITE_CONCRETE).display("-")),
        NUMBER_OVER(0, 0, head.item(9236, Material.WHITE_CONCRETE).display(" ")),
    }

    private enum class Button(override val line: Int, override val number: Int, override val item: ItemStack) : Slot {
        NUMBER_1(2, 3, head.item(8813, Material.BLACK_CONCRETE).display("1")),
        NUMBER_2(2, 4, head.item(8812, Material.BLACK_CONCRETE).display("2")),
        NUMBER_3(2, 5, head.item(8811, Material.BLACK_CONCRETE).display("3")),
        NUMBER_4(3, 3, head.item(8810, Material.BLACK_CONCRETE).display("4")),
        NUMBER_5(3, 4, head.item(8809, Material.BLACK_CONCRETE).display("5")),
        NUMBER_6(3, 5, head.item(8808, Material.BLACK_CONCRETE).display("6")),
        NUMBER_7(4, 3, head.item(8807, Material.BLACK_CONCRETE).display("7")),
        NUMBER_8(4, 4, head.item(8806, Material.BLACK_CONCRETE).display("8")),
        NUMBER_9(4, 5, head.item(8805, Material.BLACK_CONCRETE).display("9")),
        NUMBER_0(5, 4, head.item(8814, Material.BLACK_CONCRETE).display("0")),

        DIVISION(2, 6, head.item(8907, Material.BLUE_CONCRETE).display("§9÷")),
        MULTIPLY(3, 6, head.item(8950, Material.BLUE_CONCRETE).display("§9x")),
        MINUS(4, 6, head.item(8919, Material.BLUE_CONCRETE).display("§9-")),
        PLUS(5, 6, head.item(8913, Material.BLUE_CONCRETE).display("§9+")),

        BACK_SPACE(3, 7, head.item(9334, Material.RED_CONCRETE).display("§cBackSpace")),
        CLEAR(4, 7, head.item(9403, Material.RED_CONCRETE).display("§cClear")),

        CALCULATE(5, 7, head.item(9889, Material.LIME_CONCRETE).display("§a=")),
        COMPLETE(5, 8, head.item(21771, Material.LIME_CONCRETE).display("§aComplete")),
    }

    private enum class Operation(override val line: Int, override val number: Int, override val item: ItemStack) : Slot {
        BLANK(2, 0, head.item(13394, Material.LIGHT_BLUE_CONCRETE).display(" ")),
        PLUS(2, 0, head.item(10101, Material.LIGHT_BLUE_CONCRETE).display("§b+")),
        MINUS(2, 0, head.item(10107, Material.LIGHT_BLUE_CONCRETE).display("§b-")),
        MULTIPLY(2, 0, head.item(10138, Material.LIGHT_BLUE_CONCRETE).display("§bx")),
        DIVISION(2, 0, head.item(10095, Material.LIGHT_BLUE_CONCRETE).display("§b÷")),
    }

    override val plugin = Main.plugin
    override val gui = InventoryUtil.gui(6, "금액 입력")

    private var currentLong = 0L
    private var operationLong = 0L
    private var operation = Operation.BLANK


    override fun displaying() {
        gui.clear()
        setItems(Button.values())
        setItem(operation)

        if (checkOver(currentLong)) {
            val array = currentLong.toString().toCharArray()
            var slotNumber = 8
            array.reverse()
            for (char in array) {
                val int = char.digitToIntOrNull() ?: 10
                val item = DisplayNumber.values()[int].item
                gui.setItem(slotNumber, item)
                slotNumber--
            }
        } else {
            val item = DisplayNumber.NUMBER_OVER.item.clone().display(currentLong.addComma())
            gui.setItem(8, item)
        }

        if (operation == Operation.BLANK) return

        if (checkOver(operationLong)) {
            val operationIntCharArray = operationLong.toString().toCharArray()
            var slotNumber = 8
            operationIntCharArray.reverse()
            for (char in operationIntCharArray) {
                val int = char.digitToIntOrNull() ?: 0
                val item = DisplayNumber.values()[int].item
                gui.setItem(slotNumber + 9, item)
                slotNumber--
            }
        } else {
            val item = DisplayNumber.NUMBER_OVER.item.clone().display(currentLong.addComma())
            gui.setItem(9 + 8, item)
        }
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
            Button.NUMBER_1,
            Button.NUMBER_2,
            Button.NUMBER_3,
            Button.NUMBER_4,
            Button.NUMBER_5,
            Button.NUMBER_6,
            Button.NUMBER_7,
            Button.NUMBER_8,
            Button.NUMBER_9,
            Button.NUMBER_0 -> addNumber(clickItem.display().unColor().toInt())
            Button.CLEAR -> clearNumber()
            Button.BACK_SPACE -> removeNumber()
            Button.DIVISION -> setOperation(Operation.DIVISION)
            Button.MULTIPLY -> setOperation(Operation.MULTIPLY)
            Button.MINUS -> setOperation(Operation.MINUS)
            Button.PLUS -> setOperation(Operation.PLUS)
            Button.CALCULATE -> calculate()
            Button.COMPLETE -> complete()
        }
    }

    private fun addNumber(int: Int) {
        var s: String = if (operation == Operation.BLANK) currentLong.toString()
        else operationLong.toString()
        s += int.toString()
        val long = s.toLongOrNull() ?: return
        if (operation == Operation.BLANK) currentLong = long
        else operationLong = s.toLong()
        displaying()
    }

    private fun removeNumber() {
        val s: String = if (operation == Operation.BLANK) currentLong.toString()
        else operationLong.toString()
        if (s.replace("-", "").toCharArray().size == 1) {
            if (operation == Operation.BLANK) currentLong = 0
            else operationLong = 0
            return displaying()
        }
        val l = s.toCharArray().toMutableList()
        var ss = ""
        for ((i, c) in l.withIndex()) if (i != l.size - 1) ss += c
        if (operation == Operation.BLANK) currentLong = ss.toLong()
        else operationLong = ss.toLong()
        displaying()
    }

    private fun clearNumber() {
        if (operation == Operation.BLANK) currentLong = 0
        else operationLong = 0
        displaying()
    }

    private fun checkOver(long: Long) = long.toString().toCharArray().size <= 9

    private fun setOperation(operation: Operation) {
        this.operation = operation
        displaying()
    }

    private fun calculate() {
        when (operation) {
            Operation.PLUS -> currentLong += operationLong
            Operation.MINUS -> currentLong -= operationLong
            Operation.MULTIPLY -> currentLong *= operationLong
            Operation.DIVISION -> currentLong /= operationLong
            Operation.BLANK -> return
        }

        operationLong = 0
        setOperation(Operation.BLANK)
        displaying()
    }

    private fun complete() {
        CheckGUI(player, targetPlayer, currency, currentLong, processType)
    }

    init {
        openGUI()
    }
}

