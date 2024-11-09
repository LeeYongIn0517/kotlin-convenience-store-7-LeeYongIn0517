package store.domain

import store.domain.input.InputController
import store.model.OrderItem
import store.model.Product
import store.model.Receipt

class ReceiptManager(private val inputController: InputController) {
    private var receipt: Receipt = initializeReceipt()

    fun resetReceipt() {
        receipt = initializeReceipt()
    }

    private fun initializeReceipt(): Receipt {
        return Receipt("W 편의점", listOf(), listOf(), 0, 0, 0, 0, 0)
    }

    fun updateReceiptWithOrder(orderItems: List<OrderItem>) {
        receipt.items = orderItems
        calculateTotalAmount()
        calculateDiscountAmount()
        applyMembershipDiscount()
        calculateFinalAmount()
    }

    fun updateFreeItems(freeItems: List<OrderItem>) {
        receipt.freeItems = freeItems
    }

    private fun calculateTotalAmount() {
        receipt.totalPrice = receipt.items.sumOf { it.price * it.orderQuantity } +
                receipt.freeItems.sumOf { it.price * it.orderQuantity }
    }

    private fun calculateDiscountAmount() {
        receipt.promotionDiscount += receipt.freeItems.sumOf { it.price * it.orderQuantity }
    }

    private fun applyMembershipDiscount() {
        if (inputController.inputView.promptApplyMembershipDiscount()) {
            val amountAfterPromotion = receipt.totalPrice - receipt.promotionDiscount
            receipt.membershipDiscount = (amountAfterPromotion * 0.3).toInt().coerceAtMost(8000)
        } else {
            receipt.finalAmount = receipt.totalPrice - receipt.promotionDiscount
        }
    }

    private fun calculateFinalAmount() {
        receipt.finalAmount = receipt.totalPrice - receipt.promotionDiscount - receipt.membershipDiscount
    }

    fun updateProductStock(products: MutableList<Product>): List<Product> {
        return receipt.updateProductStock(products)
    }

    fun getReceipt(): Receipt = receipt
}
