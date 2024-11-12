package store.model.service.promotion

import store.model.entity.Promotion
import java.time.LocalDateTime

class PromotionValidator {
    fun isPromotionActive(promotion: Promotion, currentDate: LocalDateTime): Boolean {
        return (promotion.startDate != null && promotion.endDate != null &&
                !currentDate.isBefore(promotion.startDate) && !currentDate.isAfter(promotion.endDate))
    }
}
