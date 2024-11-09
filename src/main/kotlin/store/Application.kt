package store

import store.domain.Controller
import store.domain.StoreManager
import store.domain.product.ProductParser
import store.domain.promotion.PromotionParser
import store.domain.promotion.PromotionProcessor
import store.util.InputValidator
import store.view.InputView
import store.view.OutputView

fun main() {
    val outputView = OutputView()
    val promotionParser = PromotionParser()
    val productParser = ProductParser(promotionParser)
    val storeManager = StoreManager(productParser, promotionParser)
    val inputView = InputView(outputView)
    val promotionProcessor = PromotionProcessor(inputView)
    val inputValidator = InputValidator()
    val controller = Controller(
        inputView = inputView,
        inputValidator = inputValidator,
        storeManager = storeManager,
        outputView = outputView,
        promotionProcessor = promotionProcessor
    )
    controller.run()
}
