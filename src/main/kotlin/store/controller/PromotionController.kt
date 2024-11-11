package store.controller

import camp.nextstep.edu.missionutils.DateTimes
import store.model.entity.OrderItem
import store.model.entity.Product
import store.model.entity.Promotion
import store.model.entity.PromotionType
import store.model.service.promotion.FreeItemManager
import store.model.service.promotion.PromotionCalculator
import store.model.service.promotion.PromotionHandler
import store.model.service.promotion.PromotionValidator
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
        if (order.orderQuantity <= product.quantity) {
            val freeQuantity = promotionCalculator.calculateFreeItemQuantity(order, product)
            if (inputController.promptAddItemsForPromotion(
                    productName = order.productName,
                    additionalQuantityNeeded = freeQuantity
                )
            ) {
                println("freeQuantity: ${freeQuantity}")
                promotionCalculator.calculateDiscount(order, product)
                updateFreeItems(
                    product,
                    freeQuantity
                )
            }
            applyPromotionQuantity(order, product)
        } else { //프로모션 재고가 부족한 경우
            val requiredQuantityForPromotion = promotionCalculator.calculatePromotionQuantity(product)
            println("requiredQuantityForPromotion: ${requiredQuantityForPromotion}")
            handlePromotionWithAdditionalItems(requiredQuantityForPromotion, order, product)
        }
    }

    private fun handlePromotionWithAdditionalItems(
        requiredQuantityForPromotion: Int,
        order: OrderItem,
        product: Product
    ) {
        println("requiredQuantityForPromotion: ${requiredQuantityForPromotion}")
        val additionalQuantityNeeded = order.orderQuantity - requiredQuantityForPromotion  //추가로 받을 수 있는 수량
//        val isWantAdditionalItems =
//            inputController.promptAddItemsForPromotion(order.productName, additionalQuantityNeeded)
        if (inputController.promptPayFullPriceForShortage(
                productName = order.productName,
                quantity = additionalQuantityNeeded
            )
        ) {
            println("freeQuantity:${promotionCalculator.calculateMaximumFreeItemQuantity(product)}")
            promotionHandler.handleInsufficientPromotionQuantity(
                order,
                product,
                promotionCalculator.calculateMaximumFreeItemQuantity(product),
            )
        }
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
        product.quantity -= order.orderQuantity
    }

    private fun updateFreeItems(
        product: Product,
        freeQuantity: Int
    ) {
        freeItemManager.addFreeItem(OrderItem(product.name, freeQuantity, product.price, PromotionType.NONE))
    }
}
