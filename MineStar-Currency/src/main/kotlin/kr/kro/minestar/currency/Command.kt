package kr.kro.minestar.currency

import kr.kro.minestar.currency.Main.Companion.pl
import kr.kro.minestar.currency.Main.Companion.prefix
import kr.kro.minestar.currency.gui.CurrenciesGUI
import kr.kro.minestar.currency.gui.PlayerPurseGUI
import kr.kro.minestar.currency.value.PermissionValue
import kr.kro.minestar.utility.command.Argument
import kr.kro.minestar.utility.command.ArgumentPermission
import kr.kro.minestar.utility.command.FunctionalCommand
import kr.kro.minestar.utility.string.toPlayer
import kr.kro.minestar.utility.string.toServer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.permissions.Permission

object Command : FunctionalCommand {
    private enum class Arg(override val howToUse: String, override val permission: ArgumentPermission) : Argument {
        test("", ArgumentPermission()),
        test1("", PermissionValue.test),

        control("", PermissionValue.admin),
    }

    private fun notHavePermission(player: Player) = "$prefix §c권한이 없습니다.".toPlayer(player)

    override fun commanding(sender: CommandSender, cmd: Command, label: String, args: Array<out String>) {
        if (sender !is Player) return

        val per = Permission("kr.kro.minestar.currency.default")
        sender.addAttachment(pl, "kr.kro.minestar.currency.default", true)

        if (!PermissionValue.default.hasPermission(sender)) return notHavePermission(sender)

        if (args.isEmpty()) {
            PlayerPurseGUI(sender)
            return
        }

        val arg = argument(Arg.values(), args) ?: return

        if (!arg.isValid(args)) return "$prefix §c${arg.howToUse(label)}".toPlayer(sender)
        if (!arg.permission.hasPermission(sender)) return notHavePermission(sender)

        when (arg) {
            Arg.test -> {
                sender.permissionValue("kr.kro.minestar.currency.test")
                "TEST".toPlayer(sender)
            }
            Arg.control -> CurrenciesGUI(sender)
        }
        return
    }

    override fun onTabComplete(player: CommandSender, cmd: Command, alias: String, args: Array<out String>): List<String> {
        val list = mutableListOf<String>()

        if (player !is Player) return list

        val arg = argument(Arg.values(), args)
        val lastIndex = args.lastIndex
        val last = args.lastOrNull() ?: ""

        fun List<String>.add() {
            for (s in this) if (s.contains(last)) list.add(s)
        }

        fun Array<out Enum<*>>.add() {
            for (s in this) if (s.name.lowercase().contains(last)) list.add(s.name)
        }

        fun playerAdd() {
            for (s in Bukkit.getOnlinePlayers()) if (s.name.contains(last)) list.add(s.name)
        }


        if (arg == null) {
            Arg.values().add()
        } else when (arg) {
            Arg.test -> {}
        }
        return list
    }
}