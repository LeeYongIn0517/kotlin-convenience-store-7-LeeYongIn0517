package store.domain.promotion

import camp.nextstep.edu.missionutils.DateTimes
import store.model.OrderItem
import store.model.Product
import store.model.Promotion
import store.view.InputView
import java.time.LocalDateTime

class PromotionProcessor(
    private val inputView: InputView
) {
    // 프로모션을 적용하여 주문을 처리하는 메서드
    fun applyPromotionToOrder(
        orderItems: List<OrderItem>,
        products: MutableList<Product>
    ): Pair<Int, List<OrderItem>> {
        val freeItems = mutableListOf<OrderItem>()
        var totalPromotionDiscount = 0
        val currentDate = DateTimes.now()

        orderItems.forEach { order ->
            val product = products.find { it.name == order.productName }!!

            // 프로모션이 설정되어 있는지, 날짜가 프로모션 기간 내에 속하는지 확인
            val promotion = product.promotion
            if (promotion != null && isPromotionActive(promotion, currentDate)) {
                val discount = applyPromotion(order, product, freeItems)
                totalPromotionDiscount += discount
            } else {
                processStandardOrder(order, product)
            }
        }
        return Pair(totalPromotionDiscount, freeItems)
    }

    // 현재 날짜가 프로모션 기간 내에 있는지 확인하는 메서드
    private fun isPromotionActive(promotion: Promotion, currentDate: LocalDateTime): Boolean {
        return (promotion.startDate != null && promotion.endDate != null &&
                !currentDate.isBefore(promotion.startDate) && !currentDate.isAfter(promotion.endDate))
    }

    // 프로모션 적용 메서드
    private fun applyPromotion(
        order: OrderItem,
        product: Product,
        freeItems: MutableList<OrderItem>
    ): Int {
        val requiredQuantityForPromotion = calculatePromotionQuantity(order, product)

        return if (order.orderQuantity < requiredQuantityForPromotion) {
            handleInsufficientPromotionQuantity(order, product, requiredQuantityForPromotion, freeItems)
            0
        } else {
            val discount = calculateDiscount(order, product)
            applyPromotionQuantity(order, product, freeItems)
            discount
        }
    }

    // 프로모션에 필요한 수량 계산 메서드
    private fun calculatePromotionQuantity(order: OrderItem, product: Product): Int {
        val promotionQuantity = product.promotion!!.buy
        return (order.orderQuantity / promotionQuantity) * (promotionQuantity + 1)
    }

    // 프로모션 할인 금액 계산 메서드
    private fun calculateDiscount(order: OrderItem, product: Product): Int {
        val freeQuantity = order.orderQuantity / product.promotion!!.buy
        return freeQuantity * product.price
    }

    // 프로모션 수량이 부족한 경우 추가 수량 처리
    private fun handleInsufficientPromotionQuantity(
        order: OrderItem,
        product: Product,
        requiredQuantity: Int,
        freeItems: MutableList<OrderItem>
    ) {
        val additionalQuantityNeeded = requiredQuantity - order.orderQuantity

        // 혜택 안내 메시지 출력
        val wantsAdditionalItems = inputView.promptAddItemsForPromotion(order.productName, additionalQuantityNeeded)

        if (wantsAdditionalItems) {
            handleAdditionalPromotionQuantity(order, product, additionalQuantityNeeded, freeItems)
        }
    }

    // 추가 프로모션 수량이 있는 경우 처리
    private fun handleAdditionalPromotionQuantity(
        order: OrderItem,
        product: Product,
        additionalQuantity: Int,
        freeItems: MutableList<OrderItem>
    ) {
        val remainingPromotionStock = product.quantity - order.orderQuantity
        if (remainingPromotionStock >= additionalQuantity) {
            product.quantity -= additionalQuantity
            addFreeItem(freeItems, OrderItem(product.name, additionalQuantity, product.price))
        } else {
            promptFullPriceForShortage(order, product, additionalQuantity - remainingPromotionStock)
        }
    }

    // 정가 결제 여부 확인 및 처리
    private fun promptFullPriceForShortage(order: OrderItem, product: Product, shortageQuantity: Int) {
        val payFullPrice = inputView.promptPayFullPriceForShortage()

        if (payFullPrice) {
            product.quantity -= shortageQuantity
        }
    }

    // 프로모션 수량 적용 및 증정 상품 목록 업데이트
    private fun applyPromotionQuantity(order: OrderItem, product: Product, freeItems: MutableList<OrderItem>) {
        val freeQuantity = order.orderQuantity / product.promotion!!.buy
        addFreeItem(freeItems, OrderItem(product.name, freeQuantity, product.price))
        product.quantity -= order.orderQuantity
    }

    // 증정 상품 목록에 추가하거나, 중복될 경우 수량을 증가시키는 메서드
    private fun addFreeItem(freeItems: MutableList<OrderItem>, freeItem: OrderItem) {
        val existingItem = freeItems.find { it.productName == freeItem.productName }
        if (existingItem != null) {
            existingItem.orderQuantity += freeItem.orderQuantity
        } else {
            freeItems.add(freeItem)
        }
    }

    // 일반 주문 처리
    private fun processStandardOrder(order: OrderItem, product: Product) {
        product.quantity -= order.orderQuantity
    }
}
