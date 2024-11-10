package store.domain.validator

import store.model.OrderItem
import store.model.Product

class InputValidator {
    fun validateProductAndOrder(input: String, availableProducts: List<Product>): List<OrderItem> {
        val orderList = mutableListOf<OrderItem>()
        val orderPattern = Regex("""^(\[[\p{L}\d]+-\d+])(,(\[[\p{L}\d]+-\d+]))*$""")

        // 입력 형식이 올바르지 않은 경우 확인
        require(orderPattern.matches(input)) { ERROR_INVALID_FORMAT }

        // 입력 문자열에서 각 주문 항목을 추출하여 유효성 검증
        val itemPattern = Regex("""\[([\p{L}\d]+)-(\d+)]""")
        val matches = itemPattern.findAll(input)

        matches.forEach { matchResult ->
            val (productName, quantityStr) = matchResult.destructured
            val quantity = quantityStr.toIntOrNull()

            // 수량이 숫자가 아닌 경우, 기타 잘못된 입력 처리
            requireNotNull(quantity) { ERROR_GENERIC }
            require(quantity >= 0) { ERROR_GENERIC }

            // 존재하지 않는 상품일 경우
            val product = availableProducts.find { it.name == productName }
            requireNotNull(product) { ERROR_NON_EXISTENT_PRODUCT }

            // 수량이 재고를 초과할 경우
            require(quantity <= product.quantity) { ERROR_EXCEEDS_STOCK }

            // 유효한 상품명과 수량을 OrderItem 객체로 추가
            orderList.add(OrderItem(productName, quantity, product.price))
        }

        // 유효한 주문 항목 리스트 반환
        return orderList
    }


    companion object {
        const val ERROR_INVALID_FORMAT = "[ERROR] 올바르지 않은 형식으로 입력했습니다. 다시 입력해 주세요."
        const val ERROR_NON_EXISTENT_PRODUCT = "[ERROR] 존재하지 않는 상품입니다. 다시 입력해 주세요."
        const val ERROR_EXCEEDS_STOCK = "[ERROR] 재고 수량을 초과하여 구매할 수 없습니다. 다시 입력해 주세요."
        const val ERROR_GENERIC = "[ERROR] 잘못된 입력입니다. 다시 입력해 주세요."
    }
}
