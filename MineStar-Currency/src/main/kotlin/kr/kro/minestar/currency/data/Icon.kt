package kr.kro.minestar.currency.data

import kr.kro.minestar.utility.item.cmData
import kr.kro.minestar.utility.material.item
import org.bukkit.Material

class Icon(val material: Material, val customModelData: Int?) {
    fun item() = material.item().cmData(customModelData)
}