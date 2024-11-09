package store.model

data class Receipt(
    var storeName: String = "W 편의점",
    var items: List<OrderItem>,       // 구매한 상품 목록
    var freeItems: List<OrderItem>,        // 증정된 상품 목록
    var totalItems: Int,                  // 총 구매 개수
    var totalPrice: Int,                  // 총 구매액
    var promotionDiscount: Int,           // 행사 할인
    var membershipDiscount: Int,          // 멤버십 할인
    var finalAmount: Int                  // 최종 결제 금액 (내실돈)
) {
    // updatedProducts에서 구매한 상품과 증정된 상품의 재고를 차감하는 메서드
    fun updateProductStock(updatedProducts: MutableList<Product>): List<Product> {
        // 구매한 상품 목록 처리
        items.forEach { orderItem ->
            val product = updatedProducts.find { it.name == orderItem.productName }
            product?.let {
                it.quantity = (it.quantity - orderItem.orderQuantity).coerceAtLeast(0)
            }
        }

        // 증정된 상품 목록 처리
        freeItems.forEach { freeItem ->
            val product = updatedProducts.find { it.name == freeItem.productName }
            product?.let {
                it.quantity = (it.quantity - freeItem.orderQuantity).coerceAtLeast(0)
            }
        }
        return updatedProducts
    }
}
