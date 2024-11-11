package store.controller

import camp.nextstep.edu.missionutils.DateTimes
import store.model.entity.OrderItem
import store.model.entity.Product
import store.model.entity.Promotion
import store.model.service.promotion.*
import java.time.LocalDateTime

class PromotionController(
    private val inputController: InputController,
    private val promotionValidator: PromotionValidator,
    private val promotionCalculator: PromotionCalculator,
    private val promotionHandler: PromotionHandler,
    private val freeItemManager: FreeItemManager,
    private val itemManager: ItemManager
) {
    // 상위 레벨 메서드
    fun applyPromotionToOrders(
        orderItems: List<OrderItem>,
        products: MutableList<Product>
    ) {
        val currentDate = DateTimes.now()
        orderItems.forEach { order ->
            checkPromotionAvailability(order, products, currentDate)
        }
    }

    private fun checkPromotionAvailability(
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
    private fun processPromotion(order: OrderItem, product: Product) {
        if (isOrderQuantitySufficient(order, product)) {
            val freeQuantity = promotionCalculator.calculateFreeItemQuantity(order, product)
            processOrderWithSufficientQuantity(order, product, freeQuantity)
        } else {
            handleInsufficientOrderQuantity(order, product)
        }
    }

    private fun isOrderQuantitySufficient(order: OrderItem, product: Product): Boolean {
        return order.orderQuantity <= product.quantity
    }

    private fun processOrderWithSufficientQuantity(
        order: OrderItem,
        product: Product,
        freeQuantity: Int
    ) {
        if (isAdditionalItemsNeededForPromotion(order, product)) {
            promptAndAddAdditionalItems(order, freeQuantity)
        }
        adjustSameProductWithoutPromotion(order, product, freeQuantity)
        updateFreeItems(product, freeQuantity)
    }

    private fun adjustSameProductWithoutPromotion(
        order: OrderItem,
        product: Product,
        freeQuantity: Int
    ) {
        val sameProductWithoutPromotion = promotionHandler.findSameProductWithoutPromotion(order)
        if (sameProductWithoutPromotion != null &&
            isSameProductQuantityInsufficient(order, product, sameProductWithoutPromotion)
        ) {
            sameProductWithoutPromotion.quantity -= freeQuantity
        }
    }

    private fun isSameProductQuantityInsufficient(
        order: OrderItem,
        product: Product,
        sameProductWithoutPromotion: Product
    ): Boolean {
        return sameProductWithoutPromotion.quantity + product.quantity < order.orderQuantity
    }

    private fun handleInsufficientOrderQuantity(
        order: OrderItem,
        product: Product
    ) {
        val requiredQuantityForPromotion = promotionCalculator.calculatePromotionQuantity(product)
        handlePromotionWithAdditionalItems(requiredQuantityForPromotion, order, product)
    }

    private fun isAdditionalItemsNeededForPromotion(order: OrderItem, product: Product): Boolean {
        val promotion = product.promotion ?: return false
        return order.orderQuantity % (promotion.buy + promotion.get) != 0
    }

    private fun promptAndAddAdditionalItems(order: OrderItem, freeQuantity: Int) {
        inputController.promptAddItemsForPromotion(
            productName = order.productName,
            additionalQuantityNeeded = freeQuantity
        )
        itemManager.addItem(OrderItem(order.productName, freeQuantity, order.price))
    }

    private fun handlePromotionWithAdditionalItems(
        requiredQuantityForPromotion: Int,
        order: OrderItem,
        product: Product
    ) {
        val availableQuantity = calculateAvailableQuantity(order, requiredQuantityForPromotion)

        if (isAdditionalPaymentRequired(order, product, availableQuantity)) {
            handleInsufficientPromotion(order, product, availableQuantity)
        }
    }

    private fun isAdditionalPaymentRequired(
        order: OrderItem,
        product: Product,
        availableQuantity: Int
    ): Boolean {
        return isOrderQuantityNotMatchingPromotion(order, product) &&
                shouldPayFullPriceForShortage(order, availableQuantity)
    }

    private fun calculateAvailableQuantity(order: OrderItem, requiredQuantityForPromotion: Int): Int {
        return order.orderQuantity - requiredQuantityForPromotion
    }

    private fun isOrderQuantityNotMatchingPromotion(order: OrderItem, product: Product): Boolean {
        val promotion = product.promotion ?: return false
        return order.orderQuantity % (promotion.buy + promotion.get) != 0
    }

    private fun shouldPayFullPriceForShortage(order: OrderItem, availableQuantity: Int): Boolean {
        return inputController.promptPayFullPriceForShortage(
            productName = order.productName,
            quantity = availableQuantity
        )
    }

    private fun handleInsufficientPromotion(
        order: OrderItem,
        product: Product,
        availableQuantity: Int
    ) {
        promotionHandler.handleInsufficientPromotionQuantity(
            order,
            product,
            availableQuantity,
            promotionCalculator.calculateMaximumFreeItemQuantity(product)
        )
    }

    // 일반 주문 처리 메서드
    private fun processStandardOrder(order: OrderItem, product: Product) {
        product.quantity -= order.orderQuantity
    }

    private fun updateFreeItems(
        product: Product,
        freeQuantity: Int
    ) {
        freeItemManager.addFreeItem(OrderItem(product.name, freeQuantity, product.price))
    }
}
