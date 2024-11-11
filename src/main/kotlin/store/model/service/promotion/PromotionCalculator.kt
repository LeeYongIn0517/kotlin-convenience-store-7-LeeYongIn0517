package store.model.service.promotion

import store.model.entity.OrderItem
import store.model.entity.Product

class PromotionCalculator {
    fun calculatePromotionQuantity(product: Product): Int {
        val buyQuantity = product.promotion!!.buy
        val freeGetQuantity = product.promotion.get
        val defaultPromotionQuantity = buyQuantity + freeGetQuantity
        return (product.quantity / defaultPromotionQuantity) * (product.promotion.buy + product.promotion.get)
    }

    fun calculateFreeItemQuantity(order: OrderItem, product: Product): Int {
        val buyQuantity = product.promotion!!.buy
        val freeGetQuantity = product.promotion.get
        return (order.orderQuantity / buyQuantity) * (freeGetQuantity)
    }

    fun calculateMaximumFreeItemQuantity(product: Product): Int {
        val buyQuantity = product.promotion!!.buy
        val freeGetQuantity = product.promotion.get
        val defaultPromotionQuantity = buyQuantity + freeGetQuantity
        println((product.quantity / defaultPromotionQuantity) * freeGetQuantity)
        return (product.quantity / defaultPromotionQuantity) * freeGetQuantity
    }
}
