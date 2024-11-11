package store.model.service.promotion

import store.model.entity.OrderItem
import store.model.entity.Product
import store.model.service.StoreManager

class PromotionHandler(
    private val freeItemManager: FreeItemManager,
    private val storeManager: StoreManager
) {
    fun handleInsufficientPromotionQuantity(
        order: OrderItem,
        product: Product,
        availableQuantity: Int,
        freeItemQuantity: Int
    ) {
        if (freeItemQuantity > 0) {
            handleAdditionalPromotion(order, product, availableQuantity, freeItemQuantity)
        } else {
            handleOrderWithoutPromotion(order, product)
        }
    }

    private fun handleAdditionalPromotion(
        order: OrderItem,
        product: Product,
        availableQuantity: Int,
        freeItemQuantity: Int
    ) {
        val noProductionQuantity = calculateNoProductionQuantity(order, product)
        addFreeItem(product, freeItemQuantity)
        updateProductQuantities(product, availableQuantity, noProductionQuantity, order)
    }

    private fun calculateNoProductionQuantity(order: OrderItem, product: Product): Int {
        return order.orderQuantity - product.quantity
    }

    private fun addFreeItem(product: Product, quantity: Int) {
        freeItemManager.addFreeItem(OrderItem(product.name, quantity, product.price))
    }

    private fun updateProductQuantities(
        product: Product,
        availableQuantity: Int,
        noProductionQuantity: Int,
        order: OrderItem
    ) {
        val sameProductWithoutPromotion = findSameProductWithoutPromotion(order)
        product.quantity -= (availableQuantity - noProductionQuantity)
        sameProductWithoutPromotion?.quantity = sameProductWithoutPromotion?.quantity?.minus(noProductionQuantity)!!
    }

    private fun handleOrderWithoutPromotion(order: OrderItem, product: Product) {
        val remainingPromotionQuantity = calculateNoProductionQuantity(order, product)
        addFreeItem(product, order.orderQuantity)
        updateSameProductQuantity(order, remainingPromotionQuantity)
    }

    private fun updateSameProductQuantity(order: OrderItem, quantity: Int) {
        val sameProductWithoutPromotion = findSameProductWithoutPromotion(order)
        sameProductWithoutPromotion?.quantity = sameProductWithoutPromotion?.quantity?.minus(quantity)!!
    }

    fun findSameProductWithoutPromotion(order: OrderItem): Product? {
        return storeManager.getAvailableProducts().find {
            it.name == order.productName && it.promotion == null
        }
    }
}
