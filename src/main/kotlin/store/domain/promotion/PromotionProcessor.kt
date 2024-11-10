package store.domain.promotion

import camp.nextstep.edu.missionutils.DateTimes
import store.model.OrderItem
import store.model.Product
import java.time.LocalDateTime

class PromotionProcessor(
    private val promotionValidator: PromotionValidator,
    private val promotionCalculator: PromotionCalculator,
    private val promotionHandler: PromotionHandler,
    private val freeItemManager: FreeItemManager
) {
    fun applyPromotionToOrder(
        orderItems: List<OrderItem>,
        products: MutableList<Product>
    ) {
        val currentDate = DateTimes.now()

        orderItems.forEach { order ->
            processOrderItem(order, products, currentDate)
        }
    }

    private fun processOrderItem(
        order: OrderItem,
        products: MutableList<Product>,
        currentDate: LocalDateTime
    ) {
        val product = products.find { it.name == order.productName }!!
        val promotion = product.promotion

        if (promotion != null && promotionValidator.isPromotionActive(promotion, currentDate)) {
            processPromotion(order, product)
        } else {
            processStandardOrder(order, product)
        }
    }

    private fun processPromotion(
        order: OrderItem,
        product: Product
    ) {
        val requiredQuantityForPromotion = promotionCalculator.calculatePromotionQuantity(order, product)

        if (order.orderQuantity < requiredQuantityForPromotion) {
            promotionHandler.handleInsufficientPromotionQuantity(
                order,
                product,
                requiredQuantityForPromotion
            )
        } else {
            promotionCalculator.calculateDiscount(order, product)
            applyPromotionQuantity(order, product)
        }
    }

    private fun processStandardOrder(order: OrderItem, product: Product) {
        product.quantity -= order.orderQuantity
    }

    private fun applyPromotionQuantity(
        order: OrderItem,
        product: Product
    ) {
        updateFreeItems(order, product)
        product.quantity -= order.orderQuantity
    }

    private fun updateFreeItems(
        order: OrderItem,
        product: Product
    ) {
        val freeQuantity = order.orderQuantity / product.promotion!!.buy
        freeItemManager.addFreeItem(OrderItem(product.name, freeQuantity, product.price))
    }
}
