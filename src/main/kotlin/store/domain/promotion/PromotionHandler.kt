package store.domain.promotion

import store.model.OrderItem
import store.model.Product
import store.view.InputView

class PromotionHandler(private val inputView: InputView) {
    fun handleInsufficientPromotionQuantity(
        order: OrderItem,
        product: Product,
        requiredQuantity: Int,
        freeItems: MutableList<OrderItem>
    ) {
        val additionalQuantityNeeded = requiredQuantity - order.orderQuantity

        // 추가 수량이 필요한 경우, 사용자에게 추가 수량 요청을 묻는 메시지 출력
        val wantsAdditionalItems = inputView.promptAddItemsForPromotion(order.productName, additionalQuantityNeeded)

        if (wantsAdditionalItems) {
            handleAdditionalPromotionQuantity(order, product, additionalQuantityNeeded, freeItems)
        }
    }

    fun handleAdditionalPromotionQuantity(
        order: OrderItem,
        product: Product,
        additionalQuantity: Int,
        freeItems: MutableList<OrderItem>
    ) {
        val remainingPromotionStock = product.quantity - order.orderQuantity

        if (remainingPromotionStock >= additionalQuantity) {
            // 필요한 수량이 충분한 경우 추가 수량만큼 제품에서 차감하고 무료 품목 목록에 추가
            product.quantity -= additionalQuantity
            freeItems.add(OrderItem(product.name, additionalQuantity, product.price))
        } else {
            // 부족한 경우, 정가 지불 여부를 사용자에게 묻기
            promptFullPriceForShortage(order, product, additionalQuantity - remainingPromotionStock)
        }
    }

    fun promptFullPriceForShortage(order: OrderItem, product: Product, shortageQuantity: Int) {
        val payFullPrice = inputView.promptPayFullPriceForShortage()

        if (payFullPrice) {
            // 사용자가 정가를 지불하기로 한 경우, 제품에서 수량 차감
            product.quantity -= shortageQuantity
        }
    }
}
