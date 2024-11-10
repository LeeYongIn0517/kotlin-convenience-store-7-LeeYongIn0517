package store.domain.controller

import store.domain.StoreManager
import store.model.OrderItem
import store.model.Product
import store.view.OutputView

class Controller(
    private val outputView: OutputView,
    private val inputController: InputController,
    private val storeManager: StoreManager,
    private val promotionController: PromotionController,
    private val receiptController: ReceiptController
) {

    fun run() {
        do {
            initializeStoreAndReceipt()
            val orderItems = processOrder()
            processPromotions(orderItems)
            finalizeReceipt(orderItems)
            updateStoreInventory()

        } while (inputController.promptForAdditionalPurchase())
    }

    private fun initializeStoreAndReceipt() {
        storeManager.initializeStore()
        receiptController.resetReceipt()
    }

    private fun processOrder(): List<OrderItem> {
        val availableProducts = storeManager.getAvailableProducts()
        return getOrder(availableProducts)
    }

    private fun getOrder(availableProducts: List<Product>): List<OrderItem> {
        outputView.printProductList(availableProducts)
        return inputController.promptForValidOrder(availableProducts)
    }

    private fun processPromotions(orderItems: List<OrderItem>) {
        val availableProducts = storeManager.getAvailableProducts()
        applyPromotions(orderItems, availableProducts)
    }

    private fun applyPromotions(orderItems: List<OrderItem>, availableProducts: List<Product>) {
        promotionController.applyPromotionToOrders(
            orderItems = orderItems,
            products = availableProducts.toMutableList()
        )
        receiptController.updateReceiptFreeItems()
    }

    private fun finalizeReceipt(orderItems: List<OrderItem>) {
        receiptController.updateReceiptWithOrder(orderItems)
        receiptController.showReceipt()
    }

    private fun updateStoreInventory() {
        val availableProducts = storeManager.getAvailableProducts()
        val updatedProducts = receiptController.updateReceiptProductStocks(availableProducts)
        storeManager.updateStore(updatedProducts)
    }
}
