package kr.kro.minestar.currency.data

import kr.kro.minestar.currency.Main.Companion.plugin
import kr.kro.minestar.currency.value.FolderValue
import kr.kro.minestar.utility.bool.BooleanScript
import kr.kro.minestar.utility.bool.addScript
import kr.kro.minestar.utility.number.addComma
import kr.kro.minestar.utility.string.script
import kr.kro.minestar.utility.string.toPlayer
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class PlayerPurse(val player: Player) {
    companion object {
        private val map = hashMapOf<Player, PlayerPurse>()

        private fun registerPlayerPurse(playerPurse: PlayerPurse) {
            map[playerPurse.player] = playerPurse
        }

        fun contains(player: Player) = map.containsKey(player)

        fun getPlayerPurse(player: Player) = map[player]

    }

    init {
        registerPlayerPurse(this)
        for (currency in Currency.currencySet()) getCurrencyYaml(currency).save(currency)
    }

    //보유 금액
    fun currencyAmount(currency: Currency): Long {
        val yaml = getCurrencyYaml(currency)
        return yaml.getLong("amount")
    }

    //보유 금액 설정
    fun currencyAmountSet(currency: Currency, setAmount: Long, cause: String): BooleanScript {
        if (setAmount <= 0) return false.addScript("0 보다 작을 수 없습니다.")
        val yaml = getCurrencyYaml(currency)

        yaml["amount"] = setAmount
        yaml.save(currency)
        log(currency, "[$cause] Set ${setAmount.addComma()} [After Amount : ${currencyAmount(currency).addComma()}]")

        return true.addScript("§e$cause §f이/가 보유금액을 §e${setAmount.addComma()} §6$currency §f으/로 §e설정 §f하였습니다.")
    }

    //보유 금액 추가
    fun currencyAmountAdd(currency: Currency, addAmount: Long, cause: String): BooleanScript {
        if (addAmount <= 0) return false.addScript("0 보다 작을 수 없습니다.")
        val amount = currencyAmount(currency)
        val yaml = getCurrencyYaml(currency)

        val calcAmount = amount + addAmount

        yaml["amount"] = calcAmount
        yaml.save(currency)
        log(currency, "[$cause] Add ${addAmount.addComma()} [After Amount : ${currencyAmount(currency).addComma()}]")

        return true.addScript("§e$cause §f이/가 §e${addAmount.addComma()} §6$currency §f을/를 §a추가 §f하였습니다.")
    }

    //보유 금액 감가
    fun currencyAmountRemove(currency: Currency, removeAmount: Long, cause: String): BooleanScript {
        if (removeAmount <= 0) return false.addScript("0 보다 작을 수 없습니다.")

        val amount = currencyAmount(currency)
        val yaml = getCurrencyYaml(currency)

        var calcAmount = amount - removeAmount
        if (calcAmount < 0) calcAmount = 0

        yaml["amount"] = calcAmount
        yaml.save(currency)
        log(currency, "[$cause] Remove ${removeAmount.addComma()} [After Amount : ${currencyAmount(currency).addComma()}]")
        
        return true.addScript("§e$cause §f이/가 §e${removeAmount.addComma()} §6$currency §f을/를 §c감가 §f하였습니다.")
    }

    //지불
    fun currencyAmountPay(currency: Currency, payAmount: Long, cause: String): BooleanScript {
        if (currency.canPay()) return false.addScript("거래할 수 없는 화폐입니다.")
        if (payAmount <= 0) return false.addScript("0 보다 작을 수 없습니다.")

        val amount = currencyAmount(currency)
        val yaml = getCurrencyYaml(currency)

        val calcAmount = amount - payAmount
        if (calcAmount < 0) return false.addScript("보유금액이 충분하지 않습니다.")

        yaml["amount"] = calcAmount
        yaml.save(currency)
        log(currency, "[$cause] Pay ${payAmount.addComma()} [After Amount : ${currencyAmount(currency).addComma()}]")
        
        return true.addScript()
    }

    //수익
    fun currencyAmountEarn(currency: Currency, earnAmount: Long, cause: String): BooleanScript {
        if (currency.canPay()) return false.addScript("거래할 수 없는 화폐입니다.")
        if (earnAmount <= 0) return false.addScript("0 보다 작을 수 없습니다.")

        val amount = currencyAmount(currency)
        val yaml = getCurrencyYaml(currency)

        val calcAmount = amount + earnAmount

        yaml["amount"] = calcAmount
        yaml.save(currency)
        log(currency, "[$cause] Earn ${earnAmount.addComma()} [After Amount : ${currencyAmount(currency).addComma()}]")
        
        return true.addScript()
    }

    //송금
    fun currencyAmountSend(currency: Currency, sendAmount: Long, targetPlayer: Player, cause: String): BooleanScript {
        if (!currency.canSend()) return false.addScript("송금할 수 없는 화폐입니다.")
        if (player == targetPlayer) return false.addScript("자신에게 송금할 수 없습니다.")
        if (sendAmount <= 0) return false.addScript("0 보다 작을 수 없습니다.")

        val amount = currencyAmount(currency)
        val yaml = getCurrencyYaml(currency)

        val calcAmount = amount - sendAmount
        if (calcAmount < 0) return false.addScript("보유금액이 충분하지 않습니다.")

        val targetPurse = getPlayerPurse(targetPlayer)
            ?: return false.addScript("해당 플레이어는 오프라인 상태 이거나 존재하지 않습니다.")
        val booleanScript = targetPurse.currencyAmountReceive(currency, sendAmount, player, cause)
        if (!booleanScript.boolean) return booleanScript

        yaml["amount"] = calcAmount
        yaml.save(currency)
        log(currency, "[$cause] Send ${sendAmount.addComma()} to ${targetPlayer.name} [After Amount : ${currencyAmount(currency).addComma()}]")

        return true.addScript("§e${targetPlayer.name} §f님에게 §e${sendAmount.addComma()} §6$currency §f을/를 보냈습니다.")
    }

    //입금
    private fun currencyAmountReceive(currency: Currency, receiveAmount: Long, sendPlayer: Player, cause: String): BooleanScript {
        if (!player.isOnline) return false.addScript("해당 플레이어는 오프라인 상태입니다.")
        if (!currency.canSend()) return false.addScript("입금받을 수 없는 화폐입니다.")
        if (player == sendPlayer) return false.addScript("자신에게 입금할 수 없습니다.")
        if (receiveAmount <= 0) return false.addScript("0 보다 작을 수 없습니다.")

        val amount = currencyAmount(currency)
        val yaml = getCurrencyYaml(currency)

        val calcAmount = amount + receiveAmount

        yaml["amount"] = calcAmount
        yaml.save(currency)
        log(currency, "[$cause] Receive ${receiveAmount.addComma()} from ${sendPlayer.name} [After Amount : ${currencyAmount(currency).addComma()}]")

        val script = "§e${player.name} §f님으로부터 §e${receiveAmount.addComma()} §6$currency §f을/를 받습니다."
        script.script(plugin.prefix).toPlayer(player)
        return true.addScript(script)
    }

    /**
     * Log function
     */
    private fun log(currency: Currency, logString: String) {
        val yaml = getCurrencyLogYaml(currency)

        var numberKey = 0
        fun key() = "${dateKey()}.$numberKey"
        while (yaml.contains(key())) numberKey++

        yaml[key()] = logString
        yaml.logSave(currency)
        currencyLog(currency, logString)
    }

    private fun currencyLog(currency: Currency, logString: String) {
        val file = File(FolderValue.totalLogFolder(), "$currency-${dayDate()}.yml")
        val yaml = YamlConfiguration.loadConfiguration(file)

        var numberKey = 0
        fun key() = "${dateKey()}.$numberKey"
        while (yaml.contains(key())) numberKey++

        yaml[key()] = "<${player.name}> $logString"
        yaml.save(file)
    }

    private fun dayDate(): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(Calendar.getInstance().time)
    }

    private fun dateKey(): String {
        val format = SimpleDateFormat("yyyy-MM-dd.HH:mm:ss")
        return format.format(Calendar.getInstance().time)
    }

    /**
     * Other function
     */
    private fun getCurrencyFile(currency: Currency) = File(FolderValue.playerFolder(player), "$currency.yml").apply {
        if (!exists()) {
            val yaml = YamlConfiguration.loadConfiguration(this)
            yaml["amount"] = 0L
            yaml.save(this)
        }
    }

    private fun getCurrencyLogFile(currency: Currency) = File(FolderValue.playerFolder(player), "$currency-log.yml").apply {
        if (!exists()) {
            val yaml = YamlConfiguration.loadConfiguration(this)
            yaml.save(this)
        }
    }

    private fun getCurrencyYaml(currency: Currency) = YamlConfiguration.loadConfiguration(getCurrencyFile(currency))
    fun getCurrencyLogYaml(currency: Currency) = YamlConfiguration.loadConfiguration(getCurrencyLogFile(currency))

    private fun YamlConfiguration.save(currency: Currency) = save(getCurrencyFile(currency))
    private fun YamlConfiguration.logSave(currency: Currency) = save(getCurrencyLogFile(currency))
}