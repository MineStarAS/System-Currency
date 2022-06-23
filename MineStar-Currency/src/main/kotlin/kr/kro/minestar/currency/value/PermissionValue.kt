package kr.kro.minestar.currency.value

import kr.kro.minestar.currency.Main.Companion.plugin
import kr.kro.minestar.utility.command.ArgumentPermission

object PermissionValue {

    val default = ArgumentPermission(plugin, "default", false)
    val test = ArgumentPermission(plugin, "test", true)

    /**
     * Admin permission
     */
    val admin = ArgumentPermission(plugin, "admin", true)
}