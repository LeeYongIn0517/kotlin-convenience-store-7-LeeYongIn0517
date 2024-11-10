package store.view

import store.model.entity.Product
import store.model.entity.Receipt
import store.view.InputView.Companion.PRODUCT_COMMENT
import java.text.NumberFormat
import java.util.*

class OutputView {

    fun printProductList(products: List<Product>) {
        val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
        println(PRODUCT_COMMENT)
        products.forEach { product ->
            val formattedPrice = numberFormat.format(product.price)
            val quantityInfo = if (product.quantity == 0) "재고 없음" else "${product.quantity}개"
            val promotionInfo = product.promotion?.name ?: ""

            println("- ${product.name} ${formattedPrice}원 $quantityInfo $promotionInfo".trim())
        }
    }

    // Receipt 정보를 영수증 형식으로 출력하는 메서드
    fun printReceipt(receipt: Receipt) {
        println("=============W 편의점================")
        println("상품명\t\t수량\t금액")

        // 구매한 상품 목록 출력
        receipt.items.forEach { item ->
            println("${item.productName}\t\t${item.orderQuantity}\t${formatCurrency(item.orderQuantity * item.price)}")
        }

        println("=============증\t정===============")

        // 증정된 상품 목록 출력
        receipt.freeItems.forEach { freeItem ->
            println("${freeItem.productName}\t\t${freeItem.orderQuantity}")
        }

        println("====================================")
        println("총구매액\t\t${receipt.totalItems}\t${formatCurrency(receipt.totalPrice)}")
        println("행사할인\t\t\t-${formatCurrency(receipt.promotionDiscount)}")
        println("멤버십할인\t\t\t-${formatCurrency(receipt.membershipDiscount)}")
        println("내실돈\t\t\t ${formatCurrency(receipt.finalAmount)}")
        println()
    }

    // 금액을 1,000원 형식으로 포맷팅하는 헬퍼 메서드
    private fun formatCurrency(amount: Int): String {
        return "%,d".format(amount)
    }
}
