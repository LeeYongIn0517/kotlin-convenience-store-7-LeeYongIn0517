package store

import store.domain.ReceiptManager
import store.domain.StoreManager
import store.domain.controller.Controller
import store.domain.controller.InputController
import store.domain.controller.PromotionController
import store.domain.controller.ReceiptController
import store.domain.product.ProductParser
import store.domain.promotion.FreeItemManager
import store.domain.promotion.PromotionCalculator
import store.domain.promotion.PromotionHandler
import store.domain.promotion.PromotionParser
import store.domain.validator.InputValidator
import store.domain.validator.PromotionValidator
import store.view.InputView
import store.view.OutputView

fun main() {
    // Input 관련 객체 생성
    val inputValidator = InputValidator()
    val inputView = InputView()
    val inputController = InputController(inputView, inputValidator)

    // Output 관련 객체 생성
    val outputView = OutputView()

    // Promotion 관련 객체 생성
    val promotionParser = PromotionParser()
    val promotionCalculator = PromotionCalculator()
    val promotionValidator = PromotionValidator()
    val freeItemManager = FreeItemManager()
    val promotionHandler = PromotionHandler(freeItemManager)
    val promotionController = PromotionController(
        inputController = inputController,
        promotionValidator = promotionValidator,
        promotionCalculator = promotionCalculator,
        promotionHandler = promotionHandler,
        freeItemManager = freeItemManager
    )

    // Store 및 Product 관련 객체 생성
    val productParser = ProductParser(promotionParser)
    val storeManager = StoreManager(productParser, promotionParser)

    // Receipt 관련 객체 생성
    val receiptManager = ReceiptManager()
    val receiptController = ReceiptController(receiptManager, freeItemManager, outputView, inputController)

    // Main Controller 생성 및 실행
    val controller = Controller(
        inputController = inputController,
        storeManager = storeManager,
        outputView = outputView,
        promotionController = promotionController,
        receiptController = receiptController
    )

    controller.run()
}
