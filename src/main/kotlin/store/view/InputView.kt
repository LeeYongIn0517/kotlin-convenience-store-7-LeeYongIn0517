package store.view

import camp.nextstep.edu.missionutils.Console

class InputView(private val outputView: OutputView) {

    fun getProductOrderInput(): String {
        println(PRODUCT_INPUT)
        return Console.readLine()
    }

    // 프로모션 혜택을 받기 위해 추가 상품을 가져올지 여부 입력
    fun promptAddItemsForPromotion(productName: String, additionalQuantityNeeded: Int): Boolean {
        println(MESSAGE_PROMPT_ADD_ITEMS_FOR_PROMOTION.format(productName, additionalQuantityNeeded))
        return getYesOrNoInput()
    }

    // 프로모션 재고가 부족할 경우 일부 수량에 대해 정가로 결제할지 여부 입력
    fun promptPayFullPriceForShortage(): Boolean {
        println(MESSAGE_PROMPT_PAY_FULL_PRICE_FOR_SHORTAGE)
        return getYesOrNoInput()
    }

    // 멤버십 할인 적용 여부 입력
    fun promptApplyMembershipDiscount(): Boolean {
        println(MESSAGE_PROMPT_APPLY_MEMBERSHIP_DISCOUNT)
        return getYesOrNoInput()
    }

    // 추가 구매 여부 입력
    fun promptForAdditionalPurchase(): Boolean {
        println(MESSAGE_PROMPT_ADDITIONAL_PURCHASE)
        return getYesOrNoInput()
    }

    // Y/N 입력을 받아 Boolean 값으로 변환
    private fun getYesOrNoInput(): Boolean {
        while (true) {
            when (Console.readLine()?.trim()?.uppercase()) {
                "Y" -> return true
                "N" -> return false
                else -> println(MESSAGE_INVALID_INPUT)
            }
        }
    }

    companion object {
        const val PRODUCT_COMMENT = "안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.\n"
        const val PRODUCT_INPUT = "\n구매하실 상품명과 수량을 입력해 주세요. (예: [사이다-2],[감자칩-1])"
        const val MESSAGE_PROMPT_ADD_ITEMS_FOR_PROMOTION = "\"현재 %s은(는) %d개를 무료로 더 받을 수 있습니다. 추가하시겠습니까? (Y/N)\""
        const val MESSAGE_PROMPT_PAY_FULL_PRICE_FOR_SHORTAGE = "프로모션 혜택을 받지 못한 일부 수량을 정가로 결제하시겠습니까? (Y/N)"
        const val MESSAGE_PROMPT_APPLY_MEMBERSHIP_DISCOUNT = "멤버십 할인을 적용하시겠습니까? (Y/N)"
        const val MESSAGE_PROMPT_ADDITIONAL_PURCHASE = "추가 구매를 진행하시겠습니까? (Y/N)"
        const val MESSAGE_INVALID_INPUT = "올바른 입력을 해주세요. (Y/N)"

    }
}