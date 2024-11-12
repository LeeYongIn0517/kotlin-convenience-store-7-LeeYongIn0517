package store.model.service

import store.model.entity.OrderItem
import store.model.entity.Product
import store.model.entity.Receipt

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
        receipt.totalPrice = receipt.items.sumOf { it.price * it.orderQuantity }
    }

    fun calculateDiscountAmount() {
        receipt.promotionDiscount += receipt.freeItems.sumOf { it.price * it.orderQuantity }
    }

    fun applyMembershipDiscount(
        isApplyMembershipDiscount: Boolean,
        orderItems: List<OrderItem>,
        freeItems: List<OrderItem>
    ) {
        val uniqueInA =
            orderItems.filter { itemA -> freeItems.none { itemB -> itemB.productName == itemA.productName } }
        val uniqueInB =
            freeItems.filter { itemB -> orderItems.none { itemA -> itemA.productName == itemB.productName } }
        val result = uniqueInA + uniqueInB
        if (isApplyMembershipDiscount) {
            result.forEach {
                receipt.membershipDiscount += (it.price * it.orderQuantity * 0.3).toInt().coerceAtMost(8000)
            }
        }
    }

    fun calculateTotalItemsCount() {
        receipt.totalItemsCount = receipt.items.sumOf { it.orderQuantity }
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
