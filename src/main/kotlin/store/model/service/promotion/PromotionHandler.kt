package store.model.service.promotion

import store.model.entity.OrderItem
import store.model.entity.Product
import store.model.service.StoreManager

class PromotionHandler(private val freeItemManager: FreeItemManager, private val storeManager: StoreManager) {
    fun handleInsufficientPromotionQuantity(
        order: OrderItem,
        product: Product,
        availableQuantity: Int,
        freeItemQuantity: Int,
    ) {
        if (freeItemQuantity > 0) { //프로모션에서 증정품을 줄 수 있는 경우
            handleAdditionalPromotionQuantity(order, product, availableQuantity, freeItemQuantity)
        } else if (freeItemQuantity == 0) {
            handleOrderWithNoPromotion(order, product)
        }

    }

    fun handleAdditionalPromotionQuantity(
        order: OrderItem,
        product: Product,
        availableQuantity: Int,
        freeItemQuantity: Int
    ) {
        val noProductionQuantity = order.orderQuantity - product.quantity
        println("remainingQuantity:${noProductionQuantity}, ")
        val sameProductButNoPromotion = findSameProductButNoPromodion(order)
        freeItemManager.addFreeItem(
            OrderItem(
                product.name,
                freeItemQuantity,
                product.price
            )
        )
        product.quantity -= (availableQuantity - noProductionQuantity)
        sameProductButNoPromotion!!.quantity -= noProductionQuantity
        println("sameProductButNoPromotion: ${sameProductButNoPromotion}")
    }

    fun handleOrderWithNoPromotion(
        order: OrderItem,
        product: Product,
    ) {
        val remainingPromotionQuantity = order.orderQuantity - product.quantity
        val sameProductButNoPromotion = findSameProductButNoPromodion(order)
        freeItemManager.addFreeItem(
            OrderItem(
                order.productName,
                order.orderQuantity,
                order.price
            )
        )
        if (sameProductButNoPromotion != null) {
            sameProductButNoPromotion.quantity -= remainingPromotionQuantity
        }
    }

    fun findSameProductButNoPromodion(order: OrderItem): Product? {
        return storeManager.getAvailableProducts()
            .find { it.name == order.productName && it.promotion == null }
    }
}
