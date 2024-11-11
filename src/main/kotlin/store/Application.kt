package store

import store.controller.Controller
import store.controller.InputController
import store.controller.PromotionController
import store.controller.ReceiptController
import store.model.service.ReceiptManager
import store.model.service.StoreManager
import store.model.service.product.ProductParser
import store.model.service.promotion.*
import store.view.InputValidator
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

    // Store 및 Product 관련 객체 생성
    val productParser = ProductParser(promotionParser)
    val storeManager = StoreManager(productParser, promotionParser)

    //Promotion 관련 객체 생성
    val promotionHandler = PromotionHandler(freeItemManager, storeManager)
    val promotionController = PromotionController(
        inputController = inputController,
        promotionValidator = promotionValidator,
        promotionCalculator = promotionCalculator,
        promotionHandler = promotionHandler,
        freeItemManager = freeItemManager
    )

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
