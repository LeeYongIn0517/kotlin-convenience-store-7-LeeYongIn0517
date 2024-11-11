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
            println("freeQuantity: ${freeQuantity}")
            if (order.orderQuantity % (product.promotion!!.buy + product.promotion.get) != 0) {
                inputController.promptAddItemsForPromotion(
                    productName = order.productName,
                    additionalQuantityNeeded = freeQuantity
                )
                //applyPromotionQuantity(order, product)
                itemManager.addItem(
                    OrderItem(
                        productName = order.productName,
                        orderQuantity = freeQuantity,
                        price = order.price
                    )
                )

            }
            val sameProductButNoPromotion = promotionHandler.findSameProductButNoPromodion(order)
            val sameProductButNoPromotionQuantity = sameProductButNoPromotion?.quantity ?: 0
            if (sameProductButNoPromotion != null && sameProductButNoPromotionQuantity + product.quantity < order.orderQuantity) {
                sameProductButNoPromotion.quantity -= freeQuantity
            }
            updateFreeItems(
                product,
                freeQuantity
            )

        } else {
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
        val availableQuantity = order.orderQuantity - requiredQuantityForPromotion  //추가로 받을 수 있는 수량
//        val isWantAdditionalItems =
//            inputController.promptAddItemsForPromotion(order.productName, additionalQuantityNeeded)
        if (order.orderQuantity % (product.promotion!!.buy + product.promotion.get) != 0) {
            if (inputController.promptPayFullPriceForShortage(
                    productName = order.productName,
                    quantity = availableQuantity
                )
            ) {
                promotionHandler.handleInsufficientPromotionQuantity(
                    order,
                    product,
                    availableQuantity,
                    promotionCalculator.calculateMaximumFreeItemQuantity(product),
                )
            }
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
        freeItemManager.addFreeItem(OrderItem(product.name, freeQuantity, product.price))
    }
}
