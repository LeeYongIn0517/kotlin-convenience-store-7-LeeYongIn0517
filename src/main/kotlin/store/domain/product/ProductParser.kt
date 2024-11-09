package store.domain.product

import store.domain.promotion.PromotionParser
import store.model.Product
import java.io.File

class ProductParser(private val promotionParser: PromotionParser) {

    fun loadProducts(): List<Product> {
        val productList = mutableListOf<Product>()

        File(PATH_PRODUCTS_FILE).useLines { lines ->
            lines.drop(1).forEach { line ->
                val columns = line.split(",")
                if (columns.size >= 4) {
                    val name = columns[0].trim()
                    val price = columns[1].trim().toInt()
                    val quantity = columns[2].trim().toInt()
                    val promotionName = columns[3].trim()
                    val promotion = promotionParser.getPromotionByName(promotionName)


                    // Product 객체 생성 후 리스트에 추가
                    productList.add(Product(name, price, quantity, promotion))
                }
            }
        }

        return productList
    }

    fun saveUpdatedInventoryToFile(products: List<Product>, filePath: String = PATH_PRODUCTS_FILE) {
        // 헤더와 각 상품 정보를 CSV 형식으로 변환
        val content = buildString {
            appendLine("name,price,quantity,promotion")
            products.forEach { product ->
                appendLine("${product.name},${product.price},${product.quantity},${product.promotion?.name}")
            }
        }

        // 파일에 작성
        File(filePath).writeText(content)
    }

    companion object {
        const val PATH_PRODUCTS_FILE = "src/main/resources/products.md"
    }
}
