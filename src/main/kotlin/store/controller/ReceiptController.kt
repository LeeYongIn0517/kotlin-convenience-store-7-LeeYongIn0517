package store.controller

import store.model.entity.OrderItem
import store.model.entity.Product
import store.model.service.ReceiptManager
import store.model.service.promotion.FreeItemManager
import store.model.service.promotion.ItemManager
import store.view.OutputView

class ReceiptController(
    private val receiptManager: ReceiptManager,
    private val freeItemManager: FreeItemManager,
    private val itemManager: ItemManager,
    private val outputView: OutputView,
    private val inputController: InputController
) {
    fun updateItems(orderItems: List<OrderItem>) {
        orderItems.forEach { itemManager.addItem(it) }
    }

    fun initializeItems() {
        itemManager.initializeFreeItem()
    }

    fun getRecentItems(): List<OrderItem> {
        return itemManager.getItems()
    }

    fun updateReceiptWithOrder(orderItems: List<OrderItem>) {
        receiptManager.setReceiptItems(orderItems)
        receiptManager.calculateTotalAmount()
        receiptManager.calculateDiscountAmount()
        receiptManager.applyMembershipDiscount(
            inputController.promptApplyMembershipDiscount(),
            orderItems,
            freeItemManager.getFreeItems()
        )
        receiptManager.calculateTotalItemsCount()
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
