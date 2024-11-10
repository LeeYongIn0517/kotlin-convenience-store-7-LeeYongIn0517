package store.model.entity

data class Product(
    val name: String,
    val price: Int,
    var quantity: Int,
    val promotion: Promotion?
)
