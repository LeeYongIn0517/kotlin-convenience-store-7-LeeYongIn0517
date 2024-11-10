package store.model.service.promotion

import store.model.entity.OrderItem
import store.model.entity.Product

class PromotionHandler(private val freeItemManager: FreeItemManager) {
    fun handleInsufficientPromotionQuantity(
        order: OrderItem,
        product: Product,
        requiredQuantity: Int,
        isWantsAdditionalItems: Boolean,
        promptAsk: () -> Boolean,
    ) {
        val additionalQuantityNeeded = requiredQuantity - order.orderQuantity

        if (isWantsAdditionalItems) {
            val isPayFullPrice = promptAsk()
            handleAdditionalPromotionQuantity(order, product, additionalQuantityNeeded, isPayFullPrice)
        }
    }

    fun handleAdditionalPromotionQuantity(
        order: OrderItem,
        product: Product,
        additionalQuantity: Int,
        isPayFullPrice: Boolean
    ) {
        val remainingPromotionStock = product.quantity - order.orderQuantity

        if (remainingPromotionStock >= additionalQuantity) {
            // 필요한 수량이 충분한 경우 추가 수량만큼 제품에서 차감하고 무료 품목 목록에 추가
            product.quantity -= additionalQuantity
            freeItemManager.addFreeItem(OrderItem(product.name, additionalQuantity, product.price))
        } else {
            // 부족한 경우, 정가 지불 여부를 사용자에게 묻기
            promptFullPriceForShortage(product, additionalQuantity - remainingPromotionStock, isPayFullPrice)
        }
    }

    private fun promptFullPriceForShortage(product: Product, shortageQuantity: Int, isPayFullPrice: Boolean) {
        if (isPayFullPrice) {
            // 사용자가 정가를 지불하기로 한 경우, 제품에서 수량 차감
            product.quantity -= shortageQuantity
        }
    }
}
