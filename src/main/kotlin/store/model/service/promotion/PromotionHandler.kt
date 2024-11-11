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
        handleAdditionalPromotionQuantity(order, product, freeItemQuantity)
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
                product.price
            )
        )
        product.quantity -= remainingPromotionQuantity
        sameProductButNoPromotion!!.quantity -= remainingPromotionQuantity
    }
}
