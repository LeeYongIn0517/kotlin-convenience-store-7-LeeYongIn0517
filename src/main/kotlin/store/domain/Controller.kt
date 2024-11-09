package store.domain

import store.domain.input.InputController
import store.domain.promotion.PromotionProcessor
import store.model.OrderItem
import store.model.Product
import store.view.OutputView

class Controller(
    private val outputView: OutputView,
    private val inputController: InputController,
    private val storeManager: StoreManager,
    private val promotionProcessor: PromotionProcessor,
    private val receiptManager: ReceiptManager
) {

    fun run() {
        do {
            storeManager.initializeStore()
            receiptManager.resetReceipt()

            val availableProducts = storeManager.getAvailableProducts()
            val orderItems = getOrder(availableProducts)

            applyPromotions(orderItems, availableProducts)
            receiptManager.updateReceiptWithOrder(orderItems)

            outputView.printReceipt(receiptManager.getReceipt())

            val updatedProducts = updateStoreInventory(availableProducts)
            storeManager.updateStore(updatedProducts)

        } while (inputController.inputView.promptForAdditionalPurchase())
    }

    private fun getOrder(availableProducts: List<Product>): List<OrderItem> {
        outputView.printProductList(availableProducts)
        return inputController.promptForValidOrder(availableProducts)
    }

    private fun applyPromotions(orderItems: List<OrderItem>, availableProducts: List<Product>) {
        val freeItems = promotionProcessor.applyPromotionToOrder(
            orderItems = orderItems,
            products = availableProducts.toMutableList()
        )
        receiptManager.updateFreeItems(freeItems)
    }

    private fun updateStoreInventory(availableProducts: List<Product>): List<Product> {
        return receiptManager.updateProductStock(availableProducts.toMutableList())
    }
}
