package store.controller

import store.model.entity.OrderItem
import store.model.entity.Product
import store.model.service.ReceiptManager
import store.model.service.promotion.FreeItemManager
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
        freeItemManager.initializeFreeItem()
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
