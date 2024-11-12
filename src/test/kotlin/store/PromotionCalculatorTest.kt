package store

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import store.model.entity.OrderItem
import store.model.entity.Product
import store.model.entity.Promotion
import store.model.entity.PromotionType
import store.model.service.promotion.PromotionCalculator
import java.time.LocalDateTime

class PromotionCalculatorTest {

    private lateinit var promotionCalculator: PromotionCalculator
    private lateinit var promotion: Promotion
    private lateinit var product: Product
    private lateinit var order: OrderItem

    @BeforeEach
    fun setUp() {
        promotionCalculator = PromotionCalculator()
        promotion = Promotion(
            name = "샘플",
            type = PromotionType.BUY_N_GET_1,
            buy = 3,
            get = 1,
            startDate = LocalDateTime.of(2024, 1, 1, 0, 0),
            endDate = LocalDateTime.of(2024, 12, 1, 23, 59)
        ) // 예시: 3개 구매 시 1개 무료 제공
        product = Product(name = "샘플 상품", price = 100, quantity = 10, promotion = promotion)
        order = OrderItem(productName = "샘플 상품", orderQuantity = 9, price = 100)
    }

    @Test
    fun `상품 수량 10_프로모션 적용_총 제공 수량 계산`() {
        val result = promotionCalculator.calculatePromotionQuantity(product)
        val expected = (product.quantity / (promotion.buy + promotion.get)) * (promotion.buy + promotion.get)
        assertEquals(expected, result)
    }

    @Test
    fun `주문 수량 9_프로모션 적용_무료 항목 수량 계산`() {
        val result = promotionCalculator.calculateFreeItemQuantity(order, product)
        val expected = (order.orderQuantity / promotion.buy) * promotion.get
        assertEquals(expected, result)
    }

    @Test
    fun `상품 수량 10_최대 무료 항목 수량 계산`() {
        val result = promotionCalculator.calculateMaximumFreeItemQuantity(product)
        val expected = (product.quantity / (promotion.buy + promotion.get)) * promotion.get
        assertEquals(expected, result)
    }
}
