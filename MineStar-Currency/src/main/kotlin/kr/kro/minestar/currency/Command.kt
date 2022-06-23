package kr.kro.minestar.currency

import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.currency.data.PlayerPurse
import kr.kro.minestar.currency.gui.CurrenciesGUI
import kr.kro.minestar.currency.gui.PlayerPurseGUI
import kr.kro.minestar.currency.value.PermissionValue
import kr.kro.minestar.utility.command.*
import kr.kro.minestar.utility.string.StringColor
import kr.kro.minestar.utility.string.script
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.Bukkit

object Command : FunctionalCommand {

    enum class Arg(override val howToUse: String, override val permission: ArgumentPermission) : Argument {
        test("", PermissionValue.test), //TODO 배포할 땐 삭제해라

        help("", ArgumentPermission()),
        send("<Currency> <PlayerName> <Amount>", ArgumentPermission()),
        control("", PermissionValue.admin),
    }

    override val plugin = Main.plugin
    override val arguments = Arg.values()

    override fun isSimplePermission(): Boolean {
        return true
    }

    override fun commanding(data: CommandData, args: Array<out String>) {
        if (!data.valid) return
        val player = data.player ?: return "플레이어가 아닙니다.".script(plugin.prefix, StringColor.RED).toSender(data.sender)

        when (data.argument) {
            null -> PlayerPurseGUI(player)

            Arg.test -> { //TODO 배포할 땐 삭제해라
            }

            Arg.help -> data.printHowToUse()

            Arg.send -> {
                val currency = Currency.getCurrency(args[1]) ?: return "알 수 없는 화폐입니다.".warningScript(player)
                val targetPlayer = Bukkit.getPlayer(args[2]) ?: return "플레이어를 찾을 수 없습니다.".warningScript(player)
                val amount = args.last().toLongOrNull() ?: return "숫자를 입력하여야 합니다.".warningScript(player)

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

    override fun tabComplete(data: TabCompleteData, args: Array<out String>): MutableList<String> {
        val list = mutableListOf<String>()

        val last = data.last
        val lastIndex = data.lastIndex

        if (!data.valid) return list

        when (val argument = data.argument) {
            null -> when (lastIndex) {
                0 -> Arg.values().add(data.sender, list, last, this)
            }

            Arg.test -> {}

            Arg.send -> when (lastIndex) {
                1 -> Currency.currencyUnitList().add(list, last)
                2 -> Bukkit.getOnlinePlayers().add(list, last)
                3 -> argument.add(list, last, lastIndex)
            }

            Arg.control -> {}
        }

        return list
    }
}