package kr.kro.minestar.currency.value

import kr.kro.minestar.currency.Main.Companion.pl
import kr.kro.minestar.utility.command.ArgumentPermission

object PermissionValue {

    val test = ArgumentPermission(pl, "test")
    val command = ArgumentPermission(pl, "command")
    val send = ArgumentPermission(pl, "send")
    val pay = ArgumentPermission(pl, "pay")
    val gui = ArgumentPermission(pl, "gui")

    /**
     * Admin permission
     */
    val set = ArgumentPermission(pl, "set")
    val add = ArgumentPermission(pl, "add")
    val remove = ArgumentPermission(pl, "remove")

    val op = ArgumentPermission(pl, "op")
}