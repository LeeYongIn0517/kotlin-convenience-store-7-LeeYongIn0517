package store.domain

import store.model.OrderItem
import store.model.Product
import store.model.Receipt

class ReceiptManager {
    private var receipt: Receipt = initializeReceipt()

    // 초기화 및 설정 관련 메서드
    private fun initializeReceipt(): Receipt {
        return Receipt("W 편의점", listOf(), listOf(), 0, 0, 0, 0, 0)
    }

    fun resetReceipt() {
        receipt = initializeReceipt()
    }

    fun setReceiptItems(orderItems: List<OrderItem>) {
        receipt.items = orderItems
    }

    fun updateFreeItems(freeItems: List<OrderItem>) {
        receipt.freeItems = freeItems
    }

    // 계산 관련 메서드
    fun calculateTotalAmount() {
        receipt.totalPrice = receipt.items.sumOf { it.price * it.orderQuantity } +
                receipt.freeItems.sumOf { it.price * it.orderQuantity }
    }

    fun calculateDiscountAmount() {
        receipt.promotionDiscount += receipt.freeItems.sumOf { it.price * it.orderQuantity }
    }

    fun applyMembershipDiscount(isApplyMembershipDiscount: Boolean) {
        if (isApplyMembershipDiscount) {
            val amountAfterPromotion = receipt.totalPrice - receipt.promotionDiscount
            receipt.membershipDiscount = (amountAfterPromotion * 0.3).toInt().coerceAtMost(8000)
        } else {
            receipt.finalAmount = receipt.totalPrice - receipt.promotionDiscount
        }
    }

    fun calculateFinalAmount() {
        receipt.finalAmount = receipt.totalPrice - receipt.promotionDiscount - receipt.membershipDiscount
    }

    // 최종 처리 및 조회 관련 메서드
    fun updateProductStock(products: MutableList<Product>): List<Product> {
        return receipt.updateProductStock(products)
    }

    fun getReceipt(): Receipt = receipt
}
