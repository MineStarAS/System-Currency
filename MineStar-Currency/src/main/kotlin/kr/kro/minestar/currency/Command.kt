package kr.kro.minestar.currency

import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.currency.data.PlayerPurse
import kr.kro.minestar.currency.gui.CurrenciesGUI
import kr.kro.minestar.currency.gui.PlayerPurseGUI
import kr.kro.minestar.currency.value.PermissionValue
import kr.kro.minestar.utility.command.Argument
import kr.kro.minestar.utility.command.ArgumentPermission
import kr.kro.minestar.utility.command.FunctionalCommand
import kr.kro.minestar.utility.string.StringColor
import kr.kro.minestar.utility.string.script
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object Command : FunctionalCommand {

    enum class Arg(override val howToUse: String, override val permission: ArgumentPermission) : Argument {
        test("", ArgumentPermission(plugin, "test")), //TODO 배포할 땐 삭제해라
        send("<Currency> <PlayerName> <Amount>", ArgumentPermission(plugin, "send")),

        control("", PermissionValue.admin),
    }

    override val plugin = Main.plugin
    override val arguments = Arg.values()

    override fun commanding(data: FunctionalCommand.CommandData, args: Array<out String>) {
        if (!data.valid) return
        val player = data.player ?: return "플레이어가 아닙니다.".script(plugin.prefix, StringColor.RED).toSender(data.sender)

        when (data.argument) {
            null -> PlayerPurseGUI(player)

            Arg.test -> { //TODO 배포할 땐 삭제해라

            }

            Arg.send -> {
                val currency = Currency.getCurrency(args[1]) ?: return "알 수 없는 화폐 입니다.".warningScript(player)
                val targetPlayer = Bukkit.getPlayer(args[2]) ?: return "플레이어를 찾을 수 없습니다.".warningScript(player)
                val amount = args.last().toLongOrNull() ?: return "정수가 아닙니다.".warningScript(player)

                if (amount <= 0) return "0 보다 커야합니다.".warningScript(player)

                val playerPurse = PlayerPurse.getPlayerPurse(player)
                    ?: return "지갑 불러오기에 실패 하였습니다.(재접속 후 다시 시도 해보시기 바랍니다.)".warningScript(player)

                val booleanScript = playerPurse.currencyAmountSend(currency, amount, targetPlayer, player.name)
                if (!booleanScript.boolean) booleanScript.script.toPlayer(player)
            }

            Arg.control -> CurrenciesGUI(player)
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