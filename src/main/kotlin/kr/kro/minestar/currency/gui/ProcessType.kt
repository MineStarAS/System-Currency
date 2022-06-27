package kr.kro.minestar.currency.gui


enum class ProcessType(val string: String) {
    SEND("§b송금"),
    SET("§e금액 설정"),
    ADD("§a금액 추가"),
    REMOVE("§c금액 감가"),
    ;

    override fun toString() = string
}