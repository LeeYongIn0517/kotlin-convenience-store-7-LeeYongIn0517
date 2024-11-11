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

    fun calculateDiscount(order: OrderItem, product: Product): Int {
        val freeQuantity = order.orderQuantity / product.promotion!!.buy
        return freeQuantity * product.price
    }

    fun calculateFreeItemQuantity(product: Product): Int {
        val buyQuantity = product.promotion!!.buy
        val freeGetQuantity = product.promotion.get
        val defaultPromotionQuantity = buyQuantity + freeGetQuantity
        println((product.quantity / defaultPromotionQuantity) * (product.promotion.get))
        return (product.quantity / defaultPromotionQuantity) * (product.promotion.get)
    }
}
