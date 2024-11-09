package store

import store.domain.Controller
import store.domain.ReceiptManager
import store.domain.StoreManager
import store.domain.input.InputController
import store.domain.input.InputValidator
import store.domain.product.ProductParser
import store.domain.promotion.*
import store.view.InputView
import store.view.OutputView

fun main() {
    val inputValidator = InputValidator()
    val outputView = OutputView()
    val inputView = InputView(outputView)
    val promotionParser = PromotionParser()
    val productParser = ProductParser(promotionParser)
    val storeManager = StoreManager(productParser, promotionParser)
    val freeItemManager = FreeItemManager()
    val promotionCalculator = PromotionCalculator()
    val promotionHandler = PromotionHandler(inputView)
    val promotionValidator = PromotionValidator()
    val promotionProcessor = PromotionProcessor(
        promotionValidator = promotionValidator,
        promotionCalculator = promotionCalculator,
        promotionHandler = promotionHandler,
        freeItemManager = freeItemManager
    )
    val inputController = InputController(inputView, inputValidator)
    val receiptManager = ReceiptManager(inputController)
    val controller = Controller(
        inputController = inputController,
        storeManager = storeManager,
        outputView = outputView,
        promotionProcessor = promotionProcessor,
        receiptManager = receiptManager
    )

    controller.run()
}
