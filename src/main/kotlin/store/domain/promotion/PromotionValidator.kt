package store.domain.promotion

import store.model.Promotion
import java.time.LocalDateTime

class PromotionValidator {
    fun isPromotionActive(promotion: Promotion, currentDate: LocalDateTime): Boolean {
        return (promotion.startDate != null && promotion.endDate != null &&
                !currentDate.isBefore(promotion.startDate) && !currentDate.isAfter(promotion.endDate))
    }
}
