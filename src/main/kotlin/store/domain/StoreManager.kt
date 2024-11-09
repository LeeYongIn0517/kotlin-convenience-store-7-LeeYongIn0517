package store.domain

import store.domain.product.ProductParser
import store.domain.promotion.PromotionParser
import store.model.Product

class StoreManager(
    private val productParser: ProductParser,
    private val promotionParser: PromotionParser
) {
    private val products = mutableListOf<Product>()

    fun initializeStore() {
        promotionParser.loadPromotions()
        products.addAll(productParser.loadProducts().toMutableList())
    }

    fun updateStore(updatedProducts: List<Product>) {
        productParser.saveUpdatedInventoryToFile(updatedProducts)
    }

    fun getAvailableProducts() = products
}
