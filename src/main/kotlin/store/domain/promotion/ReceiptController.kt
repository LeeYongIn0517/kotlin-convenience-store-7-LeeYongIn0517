package store.domain.promotion

import store.domain.ReceiptManager
import store.domain.input.InputController
import store.model.OrderItem
import store.model.Product
import store.view.OutputView

class ReceiptController(
    private val receiptManager: ReceiptManager,
    private val freeItemManager: FreeItemManager,
    private val outputView: OutputView,
    private val inputController: InputController
) {
    fun updateReceiptWithOrder(orderItems: List<OrderItem>) {
        receiptManager.setReceiptItems(orderItems)
        receiptManager.calculateTotalAmount()
        receiptManager.calculateDiscountAmount()
        receiptManager.applyMembershipDiscount(inputController.promptApplyMembershipDiscount())
        receiptManager.calculateFinalAmount()
    }

    fun resetReceipt() {
        receiptManager.resetReceipt()
    }

    fun showReceipt() {
        outputView.printReceipt(receiptManager.getReceipt())
    }

    fun updateReceiptFreeItems() {
        val freeItems = freeItemManager.getFreeItems()
        receiptManager.updateFreeItems(freeItems)
    }

    fun updateReceiptProductStocks(availableProducts: List<Product>): List<Product> {
        return receiptManager.updateProductStock(availableProducts.toMutableList())
    }

}
