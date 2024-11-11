package store.model.entity

data class OrderItem(val productName: String, var orderQuantity: Int, val price: Int, val promotionType: PromotionType?)
