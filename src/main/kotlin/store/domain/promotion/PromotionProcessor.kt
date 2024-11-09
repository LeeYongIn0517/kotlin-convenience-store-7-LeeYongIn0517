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
    ): List<OrderItem> {
        val freeItems = mutableListOf<OrderItem>()
        val currentDate = DateTimes.now()

        orderItems.forEach { order ->
            processOrderItem(order, products, currentDate, freeItems)
        }

        return freeItems
    }

    private fun processOrderItem(
        order: OrderItem,
        products: MutableList<Product>,
        currentDate: LocalDateTime,
        freeItems: MutableList<OrderItem>
    ): Int {
        val product = products.find { it.name == order.productName }!!
        val promotion = product.promotion

        return if (promotion != null && promotionValidator.isPromotionActive(promotion, currentDate)) {
            processPromotion(order, product, freeItems)
        } else {
            processStandardOrder(order, product)
            0
        }
    }

    private fun processPromotion(
        order: OrderItem,
        product: Product,
        freeItems: MutableList<OrderItem>
    ): Int {
        val requiredQuantityForPromotion = promotionCalculator.calculatePromotionQuantity(order, product)

        return if (order.orderQuantity < requiredQuantityForPromotion) {
            promotionHandler.handleInsufficientPromotionQuantity(
                order,
                product,
                requiredQuantityForPromotion,
                freeItems
            )
            0
        } else {
            val discount = promotionCalculator.calculateDiscount(order, product)
            applyPromotionQuantity(order, product, freeItems)
            discount
        }
    }

    private fun applyPromotionQuantity(
        order: OrderItem,
        product: Product,
        freeItems: MutableList<OrderItem>
    ) {
        val freeQuantity = order.orderQuantity / product.promotion!!.buy
        freeItemManager.addFreeItem(freeItems, OrderItem(product.name, freeQuantity, product.price))
        product.quantity -= order.orderQuantity
    }

    private fun processStandardOrder(order: OrderItem, product: Product) {
        product.quantity -= order.orderQuantity
    }
}
