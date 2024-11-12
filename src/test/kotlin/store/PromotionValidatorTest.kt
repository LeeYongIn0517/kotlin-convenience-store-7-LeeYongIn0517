package store

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import store.model.entity.Promotion
import store.model.entity.PromotionType
import store.model.service.promotion.PromotionValidator
import java.time.LocalDateTime

class PromotionValidatorTest {

    private lateinit var promotionValidator: PromotionValidator
    private lateinit var promotion: Promotion

    @BeforeEach
    fun setUp() {
        promotionValidator = PromotionValidator()
        promotion = Promotion(
            name = "샘플 프로모션",
            type = PromotionType.BUY_N_GET_1,
            buy = 3,
            get = 1,
            startDate = LocalDateTime.of(2024, 1, 1, 0, 0),
            endDate = LocalDateTime.of(2024, 12, 1, 23, 59)
        )
    }

    @Test
    fun `프로모션 시작일 이전 날짜_프로모션 비활성`() {
        val currentDate = LocalDateTime.of(2023, 12, 31, 23, 59)
        val result = promotionValidator.isPromotionActive(promotion, currentDate)
        assertFalse(result)
    }

    @Test
    fun `프로모션 시작일_프로모션 활성`() {
        val currentDate = LocalDateTime.of(2024, 1, 1, 0, 0)
        val result = promotionValidator.isPromotionActive(promotion, currentDate)
        assertTrue(result)
    }

    @Test
    fun `프로모션 기간 중간 날짜_프로모션 활성`() {
        val currentDate = LocalDateTime.of(2024, 6, 1, 12, 0)
        val result = promotionValidator.isPromotionActive(promotion, currentDate)
        assertTrue(result)
    }

    @Test
    fun `프로모션 종료일_프로모션 활성`() {
        val currentDate = LocalDateTime.of(2024, 12, 1, 23, 59)
        val result = promotionValidator.isPromotionActive(promotion, currentDate)
        assertTrue(result)
    }

    @Test
    fun `프로모션 종료일 이후 날짜_프로모션 비활성`() {
        val currentDate = LocalDateTime.of(2024, 12, 2, 0, 0)
        val result = promotionValidator.isPromotionActive(promotion, currentDate)
        assertFalse(result)
    }
}
