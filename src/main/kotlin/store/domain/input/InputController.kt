package store.domain.input

import store.model.OrderItem
import store.model.Product
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
}
