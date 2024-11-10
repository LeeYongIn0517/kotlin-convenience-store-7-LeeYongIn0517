package store.domain.controller

import camp.nextstep.edu.missionutils.DateTimes
import store.domain.promotion.FreeItemManager
import store.domain.promotion.PromotionCalculator
import store.domain.promotion.PromotionHandler
import store.domain.validator.PromotionValidator
import store.model.OrderItem
import store.model.Product
import store.model.Promotion
import java.time.LocalDateTime

class PromotionController(
    private val inputController: InputController,
    private val promotionValidator: PromotionValidator,
    private val promotionCalculator: PromotionCalculator,
    private val promotionHandler: PromotionHandler,
    private val freeItemManager: FreeItemManager
) {
    // 상위 레벨 메서드
    fun applyPromotionToOrders(
        orderItems: List<OrderItem>,
        products: MutableList<Product>
    ) {
        val currentDate = DateTimes.now()

        orderItems.forEach { order ->
            checkPromotionAvailabilty(order, products, currentDate)
        }
    }

    private fun checkPromotionAvailabilty(
        order: OrderItem,
        products: MutableList<Product>,
        currentDate: LocalDateTime
    ) {
        val product = products.find { it.name == order.productName }!!

        if (isPromotionAvailable(product.promotion, currentDate)) {
            processPromotion(order, product)
        } else {
            processStandardOrder(order, product)
        }
    }

    private fun isPromotionAvailable(promotion: Promotion?, currentDate: LocalDateTime): Boolean {
        return promotion != null && promotionValidator.isPromotionActive(promotion, currentDate)
    }

    // 프로모션 처리 관련 메서드
    private fun processPromotion(
        order: OrderItem,
        product: Product
    ) {
        val requiredQuantityForPromotion = promotionCalculator.calculatePromotionQuantity(order, product)

        if (order.orderQuantity < requiredQuantityForPromotion) {
            handlePromotionWithAdditionalItems(requiredQuantityForPromotion, order, product)
        } else {
            promotionCalculator.calculateDiscount(order, product)
            applyPromotionQuantity(order, product)
        }
    }

    private fun handlePromotionWithAdditionalItems(
        requiredQuantityForPromotion: Int,
        order: OrderItem,
        product: Product
    ) {
        val additionalQuantityNeeded = requiredQuantityForPromotion - order.orderQuantity
        val isWantAdditionalItems =
            inputController.promptAddItemsForPromotion(order.productName, additionalQuantityNeeded)
        promotionHandler.handleInsufficientPromotionQuantity(
            order,
            product,
            requiredQuantityForPromotion,
            isWantAdditionalItems,
            { inputController.promptPayFullPriceForShortage() }
        )
    }

    // 일반 주문 처리 메서드
    private fun processStandardOrder(order: OrderItem, product: Product) {
        product.quantity -= order.orderQuantity
    }

    // 프로모션 적용 관련 메서드
    private fun applyPromotionQuantity(
        order: OrderItem,
        product: Product
    ) {
        updateFreeItems(order, product)
        product.quantity -= order.orderQuantity
    }

    private fun updateFreeItems(
        order: OrderItem,
        product: Product
    ) {
        val freeQuantity = order.orderQuantity / product.promotion!!.buy
        freeItemManager.addFreeItem(OrderItem(product.name, freeQuantity, product.price))
    }
}
