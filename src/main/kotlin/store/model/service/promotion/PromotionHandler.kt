package store.model.service.promotion

import store.model.entity.OrderItem
import store.model.entity.Product
import store.model.entity.PromotionType
import store.model.service.StoreManager

class PromotionHandler(private val freeItemManager: FreeItemManager, private val storeManager: StoreManager) {
    fun handleInsufficientPromotionQuantity(
        order: OrderItem,
        product: Product,
        freeItemQuantity: Int,
    ) {
        if (freeItemQuantity > 0) { //프로모션에서 증정품을 줄 수 있는 경우
            handleAdditionalPromotionQuantity(order, product, freeItemQuantity)
        } else if (freeItemQuantity == 0) {
            handleOrderWithNoPromotion(order, product)
        }

    }

    fun handleAdditionalPromotionQuantity(
        order: OrderItem,
        product: Product,
        freeItemQuantity: Int
    ) {
        val remainingPromotionQuantity = order.orderQuantity - product.quantity
        val sameProductButNoPromotion = storeManager.getAvailableProducts()
            .find { it.name == order.productName && it.promotion?.type == PromotionType.NONE }
        freeItemManager.addFreeItem(
            OrderItem(
                product.name,
                freeItemQuantity,
                product.price,
                product.promotion!!.type
            )
        )
        sameProductButNoPromotion!!.quantity -= remainingPromotionQuantity
        println("sameProductButNoPromotion: ${sameProductButNoPromotion}")
    }

    fun handleOrderWithNoPromotion(
        order: OrderItem,
        product: Product,
    ) {
        val remainingPromotionQuantity = order.orderQuantity - product.quantity
        val sameProductButNoPromotion = storeManager.getAvailableProducts()
            .find { it.name == order.productName && it.promotion?.type != product.promotion?.type }
        freeItemManager.addFreeItem(
            OrderItem(
                order.productName,
                order.orderQuantity,
                order.price,
                PromotionType.NONE
            )
        )
        if (sameProductButNoPromotion != null) {
            sameProductButNoPromotion.quantity -= remainingPromotionQuantity
        }
    }
}
