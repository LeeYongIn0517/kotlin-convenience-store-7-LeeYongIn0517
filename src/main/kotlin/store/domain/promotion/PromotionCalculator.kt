package store.domain.promotion

import store.model.OrderItem
import store.model.Product

class PromotionCalculator {
    fun calculatePromotionQuantity(order: OrderItem, product: Product): Int {
        val promotionQuantity = product.promotion!!.buy
        return (order.orderQuantity / promotionQuantity) * (promotionQuantity + 1)
    }

    fun calculateDiscount(order: OrderItem, product: Product): Int {
        val freeQuantity = order.orderQuantity / product.promotion!!.buy
        return freeQuantity * product.price
    }
}
