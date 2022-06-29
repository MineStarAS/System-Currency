package kr.kro.minestar.currency

import kr.kro.minestar.currency.data.Currency
import kr.kro.minestar.currency.data.PlayerPurse
import kr.kro.minestar.currency.function.ConfigClass
import kr.kro.minestar.currency.gui.CurrenciesGUI
import kr.kro.minestar.currency.gui.PlayerPurseGUI
import kr.kro.minestar.currency.value.PermissionValue
import kr.kro.minestar.utility.bool.BooleanScript
import kr.kro.minestar.utility.command.*
import kr.kro.minestar.utility.number.addComma
import kr.kro.minestar.utility.string.script
import org.bukkit.Bukkit

object Command : FunctionalCommand {

    enum class Arg : Argument {
//        test("", PermissionValue.test), //TODO 배포할 땐 주석처리

        help(listOf("도움말"), "", ArgumentPermission()),
        send(listOf("송금"), "<Currency> <PlayerName> <Amount>", PermissionValue.default),

        control(listOf("컨트롤"), "", PermissionValue.admin),
        set(listOf("지정"), "<Currency> <PlayerName> <Amount>", PermissionValue.admin),
        add(listOf("추가"), "<Currency> <PlayerName> <Amount>", PermissionValue.admin),
        remove(listOf("감가"), "<Currency> <PlayerName> <Amount>", PermissionValue.admin),

        create(listOf("생성"), "<CurrencyName>", PermissionValue.admin),
        delete(listOf("삭제"), "<Currency>", PermissionValue.admin),
        icon(listOf("아이콘"), "<Currency>", PermissionValue.admin),

        can(listOf("가능"), "<Currency> [send/pay] [true/false]", PermissionValue.admin),
        ;

        override val howToUse: String
        override val permission: ArgumentPermission
        override val aliases: List<String>?

        constructor(howToUse: String, permission: ArgumentPermission) {
            this.howToUse = howToUse
            this.permission = permission
            this.aliases = null
        }

        constructor(aliases: List<String>, howToUse: String, permission: ArgumentPermission) {
            this.howToUse = howToUse
            this.permission = permission
            this.aliases = aliases
        }
    }

    override val plugin = Main.plugin
    override val arguments = Arg.values()

    override fun isSimplePermission() = ConfigClass().simplePermission

    override fun commanding(data: CommandData, args: Array<out String>) {
        if (!data.valid) return
        val player = data.player ?: return "플레이어가 아닙니다.".warningScript(data.sender)

        when (data.argument) {
            null -> PlayerPurseGUI(player)

//            Arg.test -> {} //TODO 배포할 땐 주석처리

            Arg.help -> data.printHowToUse()

            Arg.control -> CurrenciesGUI(player)

            Arg.send,
            Arg.set,
            Arg.add,
            Arg.remove -> {
                val currency = Currency.getCurrency(args[1]) ?: return "알 수 없는 화폐입니다.".warningScript(player)
                val targetPlayer = Bukkit.getPlayer(args[2]) ?: return "플레이어를 찾을 수 없습니다.".warningScript(player)
                val amount = args.last().toLongOrNull() ?: return "숫자를 입력하여야 합니다.".warningScript(player)

                if (amount <= 0) return "0 보다 커야합니다.".warningScript(player)

                val playerPurse: PlayerPurse = when (data.argument) {
                    Arg.send -> PlayerPurse.getPlayerPurse(player)
                        ?: return "지갑 불러오기에 실패 하였습니다.(재접속 후 다시 시도 해보시기 바랍니다.)".warningScript(player)
                    Arg.set,
                    Arg.add,
                    Arg.remove -> PlayerPurse.getPlayerPurse(targetPlayer)
                        ?: return "지갑 불러오기에 실패 하였습니다.(대상이 재접속 후 다시 시도 해보시기 바랍니다.)".warningScript(player)
                    else -> return
                }

                val prefix = "§6$currency"

                val booleanScript: BooleanScript
                when (data.argument) {
                    Arg.send -> {
                        booleanScript = playerPurse.currencyAmountSend(currency, amount, targetPlayer, player.name)
                    }

                    Arg.set -> {
                        booleanScript = playerPurse.currencyAmountSet(currency, amount, player.name)
                        if (booleanScript.boolean)
                            "§e${targetPlayer.name} §f님의 보유금액을 §e${amount.addComma()} §6$currency §f으/로 §e설정 §f하였습니다."
                                .script(prefix).finishScript(player)
                    }
                    Arg.add -> {
                        booleanScript = playerPurse.currencyAmountAdd(currency, amount, player.name)
                        if (booleanScript.boolean)
                            "§e${targetPlayer.name} §f님에게 §e${amount.addComma()} §6$currency §f을/를 §a추가 §f하였습니다."
                                .script(prefix).finishScript(player)
                    }
                    Arg.remove -> {
                        booleanScript = playerPurse.currencyAmountRemove(currency, amount, player.name)
                        if (booleanScript.boolean)
                            "§e${targetPlayer.name} §f님에게 §e${amount.addComma()} §6$currency §f을/를 §c감가 §f하였습니다."
                                .script(prefix).finishScript(player)
                    }
                    else -> return
                }

                if (booleanScript.boolean) booleanScript.script.script(prefix).finishScript(player)
                else booleanScript.script.warningScript(player)
            }

            Arg.create -> {
                val unit = args[1]

                if (Currency.contains(unit)) return "이미 존재하는 화폐입니다.".warningScript(player)

                val currency = Currency(unit)

                "§6$currency §f을/를 §a생성§f하였습니다.".finishScript(player)
            }

            Arg.delete -> {
                val currency = Currency.getCurrency(args[1]) ?: return "알 수 없는 화폐입니다.".warningScript(player)

                currency.delete()

                "§6$currency §f을/를 §c삭제§f하였습니다.".finishScript(player)
            }

            Arg.icon -> {
                val currency = Currency.getCurrency(args[1]) ?: return "알 수 없는 화폐입니다.".warningScript(player)

                val item = player.inventory.itemInMainHand

                if (item.type.isAir) return "손에 아이템을 들고 사용해 주시기 바랍니다.".warningScript(player)
                if (!item.type.isItem) return "아이템이 아닙니다.".warningScript(player)

                currency.icon(item)

                "§a아이콘이 등록되었습니다.".finishScript(player)
            }

            Arg.can -> {
                val currency = Currency.getCurrency(args[1]) ?: return "알 수 없는 화폐입니다.".warningScript(player)

                val boolean = args.last().toBooleanStrictOrNull() ?: return data.argument!!.howToUse.warningScript(player)

                when (args[2]) {
                    "send" -> {
                        currency.canSend(boolean)
                        val booleanText = if (boolean) "§a가능"
                        else "§c불가"
                        "§6$currency§f의 §b송금 §f가능 여부를 $booleanText§f로 설정하였습니다.".finishScript(player)
                    }

                    "pay" -> {
                        currency.canPay(boolean)
                        val booleanText = if (boolean) "§a가능"
                        else "§c불가"
                        "§6$currency§f의 §b지불 §f가능 여부를 $booleanText§f로 설정하였습니다.".finishScript(player)

                    }

                    else -> data.argument!!.howToUse.warningScript(player)
                }
            }
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

//            Arg.test -> {}

            Arg.send,
            Arg.set,
            Arg.add,
            Arg.remove -> when (lastIndex) {
                1 -> Currency.currencyUnitList().add(list, last)
                2 -> Bukkit.getOnlinePlayers().add(list, last)
                3 -> argument.add(list, last, lastIndex)
            }

            Arg.create -> when (lastIndex) {
                1 -> argument.add(list, last, lastIndex)
            }
            Arg.delete,
            Arg.icon -> when (lastIndex) {
                1 -> Currency.currencyUnitList().add(list, last)
            }

            Arg.can -> when (lastIndex) {
                1 -> Currency.currencyUnitList().add(list, last)
                2, 3 -> argument.argList(lastIndex).add(list, last)
            }

            Arg.control -> {}
        }

        return list
    }
}