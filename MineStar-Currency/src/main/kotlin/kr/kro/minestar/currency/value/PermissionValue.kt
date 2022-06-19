package kr.kro.minestar.currency.value

import kr.kro.minestar.currency.Main.Companion.pl
import kr.kro.minestar.utility.command.ArgumentPermission

object PermissionValue {

    val default = ArgumentPermission(pl, "default")
    val test = ArgumentPermission(pl, "test")

    /**
     * Admin permission
     */
    val admin = ArgumentPermission(pl, "admin")
}