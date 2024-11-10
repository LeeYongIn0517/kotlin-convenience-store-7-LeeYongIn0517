package store.domain

import store.domain.input.InputController
import store.domain.promotion.PromotionProcessor
import store.domain.promotion.ReceiptController
import store.model.OrderItem
import store.model.Product
import store.view.OutputView

class Controller(
    private val outputView: OutputView,
    private val inputController: InputController,
    private val storeManager: StoreManager,
    private val promotionProcessor: PromotionProcessor,
    private val receiptController: ReceiptController
) {

    fun run() {
        do {
            storeManager.initializeStore()
            receiptController.resetReceipt()

            val availableProducts = storeManager.getAvailableProducts()
            val orderItems = getOrder(availableProducts)

            applyPromotions(orderItems, availableProducts)
            receiptController.updateReceiptWithOrder(orderItems)

            receiptController.showReceipt()

            val updatedProducts = receiptController.updateReceiptProductStocks(availableProducts)
            storeManager.updateStore(updatedProducts)

        } while (inputController.promptForAdditionalPurchase())
    }

    private fun getOrder(availableProducts: List<Product>): List<OrderItem> {
        outputView.printProductList(availableProducts)
        return inputController.promptForValidOrder(availableProducts)
    }

    private fun applyPromotions(orderItems: List<OrderItem>, availableProducts: List<Product>) {
        promotionProcessor.applyPromotionToOrder(
            orderItems = orderItems,
            products = availableProducts.toMutableList()
        )
        receiptController.updateReceiptFreeItems()
    }
}
