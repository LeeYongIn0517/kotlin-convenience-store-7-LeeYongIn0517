package store.domain

import store.domain.promotion.PromotionProcessor
import store.model.OrderItem
import store.model.Product
import store.model.Receipt
import store.util.InputValidator
import store.view.InputView
import store.view.OutputView

class Controller(
    private val inputView: InputView,
    private val outputView: OutputView,
    private val inputValidator: InputValidator,
    private val storeManager: StoreManager,
    private val promotionProcessor: PromotionProcessor
) {
    private var receipt = Receipt("W 편의점", listOf(), listOf(), 0, 0, 0, 0, 0)
    fun run() {
        do {
            // 스토어 초기화
            storeManager.initializeStore()

            // 이용 가능한 상품 목록을 가져오기
            val availableProducts = storeManager.getAvailableProducts()

            // 주문 항목 생성
            val orderItems = getOrder(availableProducts)

            // 프로모션 적용
            checkPromotion(orderItems = orderItems, availableProducts = availableProducts)

            // 영수증 업데이트
            receipt.items = orderItems

            // 총 금액 업데이트
            calculatePureTotalAmount()

            // 할인 업데이트
            calculateDiscountAmount()

            // 멤버십 할인 적용 여부 및 업데이트
            checkMembership()

            // 최종 결제 금액 업데이트
            calculateFinalAmount()

            // 영수증 출력
            outputView.printReceipt(receipt)

            // 재고 업데이트
            val updatedProducts = updateStoreManage(availableProducts)

            // products.md 파일 업데이트
            storeManager.updateStore(updatedProducts)

            // 추가 구매 여부 확인
        } while (inputView.promptForAdditionalPurchase())

    }


    fun getOrder(availableProducts: List<Product>): List<OrderItem> {
        outputView.printProductList(availableProducts)
        return promptForValidOrder(availableProducts = availableProducts)
    }

    fun checkPromotion(orderItems: List<OrderItem>, availableProducts: List<Product>) {
        //n+1 상품 프로모션 계산
        val updatedInfo = promotionProcessor.applyPromotionToOrder(
            orderItems = orderItems,
            products = availableProducts.toMutableList()
        )
        //영수증 할인금액, 증정목록 업데이트
        receipt.promotionDiscount = updatedInfo.first
        receipt.freeItems = updatedInfo.second
    }

    fun calculatePureTotalAmount() {
        receipt.items.forEach {
            receipt.totalPrice = receipt.totalPrice.plus(it.price * it.orderQuantity)
        }
        receipt.freeItems.forEach {
            receipt.totalPrice = receipt.totalPrice.plus(it.price * it.orderQuantity)
        }
    }

    fun calculateDiscountAmount() {
        receipt.freeItems.forEach {
            receipt.promotionDiscount = receipt.promotionDiscount.plus(it.price * it.orderQuantity)
        }
    }

    fun checkMembership() {
        // 멤버십 할인 적용 여부 확인
        val applyMembershipDiscount = inputView.promptApplyMembershipDiscount()
        if (applyMembershipDiscount) {
            // 프로모션 적용 후 남은 금액 계산
            val amountAfterPromotion = receipt.totalPrice.minus(receipt.promotionDiscount)

            // 멤버십 할인 계산: 30% 적용하되, 최대 8,000원까지 할인
            val membershipDiscount = (amountAfterPromotion.times(0.3)).toInt().coerceAtMost(8000)

            // 멤버십 할인과 최종 결제 금액 갱신
            receipt.membershipDiscount = membershipDiscount
        } else {
            // 멤버십 할인을 적용하지 않을 경우, 프로모션 할인만 반영하여 최종 결제 금액 설정
            receipt.finalAmount = receipt.totalPrice.minus(receipt.promotionDiscount)
        }
    }

    fun calculateFinalAmount() {
        receipt.finalAmount = receipt.totalPrice - receipt.promotionDiscount - receipt.membershipDiscount
    }

    fun updateStoreManage(availableProducts: List<Product>): List<Product> {
        return receipt.updateProductStock(availableProducts.toMutableList())
    }

}
