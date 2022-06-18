package kr.kro.minestar.currency.value

import kr.kro.minestar.currency.Main.Companion.pl
import kr.kro.minestar.utility.command.ArgumentPermission

object PermissionValue {

    val test = ArgumentPermission(pl, "test")
    val default = ArgumentPermission(pl, "default")

    /**
     * Admin permission
     */
    val set = ArgumentPermission(pl, "set")
    val add = ArgumentPermission(pl, "add")
    val remove = ArgumentPermission(pl, "remove")

    val op = ArgumentPermission(pl, "op")
}