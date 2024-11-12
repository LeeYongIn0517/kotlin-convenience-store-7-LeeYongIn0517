package store.view

import store.model.entity.Product
import store.model.entity.Receipt
import java.text.NumberFormat
import java.util.*

class OutputView {

    fun printProductList(products: List<Product>) {
        val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
        println(PRODUCT_COMMENT)
        products.forEach { product ->
            val formattedPrice = numberFormat.format(product.price)
            val quantityInfo = if (product.quantity == 0) NO_STOCK else "${product.quantity}개"
            val promotionInfo = product.promotion?.name ?: ""

            println("- ${product.name} ${formattedPrice}원 $quantityInfo $promotionInfo".trim())
        }
    }

    // Receipt 정보를 영수증 형식으로 출력하는 메서드
    fun printReceipt(receipt: Receipt) {
        println(STORE_NAME)
        println(PRODUCT_HEADER)

        // 구매한 상품 목록 출력
        receipt.items.forEach { item ->
            println("${item.productName}\t\t${item.orderQuantity}\t${formatCurrency(item.orderQuantity * item.price)}")
        }

        println(FREE_ITEM_HEADER)

        // 증정된 상품 목록 출력
        receipt.freeItems.forEach { freeItem ->
            println("${freeItem.productName}\t\t${freeItem.orderQuantity}")
        }

        println(RECEIPT_SEPARATOR)
        println("$TOTAL_PURCHASE\t\t${receipt.totalItemsCount}\t${formatCurrency(receipt.totalPrice)}")
        println("$PROMOTION_DISCOUNT\t\t\t-${formatCurrency(receipt.promotionDiscount)}")
        println("$MEMBERSHIP_DISCOUNT\t\t\t-${formatCurrency(receipt.membershipDiscount)}")
        println("$FINAL_AMOUNT\t\t\t ${formatCurrency(receipt.finalAmount)}")
        println()
    }

    // 금액을 1,000원 형식으로 포맷팅하는 헬퍼 메서드
    private fun formatCurrency(amount: Int): String {
        return "%,d".format(amount)
    }

    companion object {
        const val PRODUCT_COMMENT = "안녕하세요. W편의점입니다.\n현재 보유하고 있는 상품입니다.\n"
        const val STORE_NAME = "=============W 편의점================"
        const val PRODUCT_HEADER = "상품명\t\t수량\t금액"
        const val FREE_ITEM_HEADER = "=============증\t정==============="
        const val RECEIPT_SEPARATOR = "===================================="
        const val TOTAL_PURCHASE = "총구매액"
        const val PROMOTION_DISCOUNT = "행사할인"
        const val MEMBERSHIP_DISCOUNT = "멤버십할인"
        const val FINAL_AMOUNT = "내실돈"
        const val NO_STOCK = "재고 없음"
    }

}
