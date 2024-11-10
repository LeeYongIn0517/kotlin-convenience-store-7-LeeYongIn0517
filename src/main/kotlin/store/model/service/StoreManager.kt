package store.model.service

import store.model.service.product.ProductParser
import store.model.service.promotion.PromotionParser
import store.model.entity.Product

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
