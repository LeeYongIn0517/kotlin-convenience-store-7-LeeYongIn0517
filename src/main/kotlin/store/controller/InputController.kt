package store.controller

import store.model.entity.OrderItem
import store.model.entity.Product
import store.view.InputValidator
import store.view.InputView

class InputController(private val inputView: InputView, private val inputValidator: InputValidator) {
    fun promptForValidOrder(availableProducts: List<Product>): List<OrderItem> {
        while (true) {
            try {
                val productAndOrder = inputView.getProductOrderInput()
                val validatedOrder = inputValidator.validateProductAndOrder(productAndOrder, availableProducts)
                return validatedOrder
            } catch (e: IllegalArgumentException) {
                println(e.message)
            }
        }
    }

    fun promptForAdditionalPurchase(): Boolean {
        return inputView.promptForAdditionalPurchase()
    }

    fun promptApplyMembershipDiscount(): Boolean {
        return inputView.promptApplyMembershipDiscount()
    }

    fun promptAddItemsForPromotion(productName: String, additionalQuantityNeeded: Int): Boolean {
        return inputView.promptAddItemsForPromotion(productName, additionalQuantityNeeded)
    }

    fun promptPayFullPriceForShortage(): Boolean {
        return inputView.promptPayFullPriceForShortage()
    }
}
