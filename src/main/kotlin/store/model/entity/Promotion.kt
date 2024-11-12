package store.model.entity

import java.time.LocalDateTime

data class Promotion(
    val name: String,
    val type: PromotionType?,
    var buy: Int,
    val get: Int,
    val startDate: LocalDateTime,
    val endDate: LocalDateTime
)
