package store.domain.promotion

import store.model.Promotion
import store.model.PromotionType
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class PromotionParser {
    // 프로모션 정보를 담을 맵
    private val promotionMap = mutableMapOf<String, Promotion>()

    // 날짜 형식 지정
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // promotions.md 파일 로드 및 파싱
    fun loadPromotions(): Map<String, Promotion> {
        val filePath = "src/main/resources/promotions.md"

        File(filePath).useLines { lines ->
            lines.drop(1).forEach { line -> // 헤더를 건너뛰기 위해 drop(1) 사용
                val columns = line.split(",")
                if (columns.size >= 5) {
                    val name = columns[0].trim()
                    val buy = columns[1].trim().toInt()
                    val get = columns[2].trim().toInt()
                    // LocalDate로 파싱한 후 LocalDateTime으로 변환
                    val startDateTime = LocalDate.parse(columns[3].trim(), dateFormatter).atStartOfDay()
                    val endDateTime = LocalDate.parse(columns[4].trim(), dateFormatter).atStartOfDay()


                    val type = when {
                        name == "반짝할인" -> PromotionType.DISCOUNT
                        name == "MD추천상품" -> PromotionType.MD_RECOMMENDATION
                        name.matches(Regex("\\d+\\+1")) -> PromotionType.BUY_N_GET_1
                        else -> PromotionType.NONE
                    }

                    val promotion = Promotion(name, type, buy, get, startDateTime, endDateTime)
                    promotionMap[name] = promotion
                }
            }
        }
        return promotionMap
    }

    fun getPromotionByName(name: String): Promotion? {
        return promotionMap[name]
    }
}
