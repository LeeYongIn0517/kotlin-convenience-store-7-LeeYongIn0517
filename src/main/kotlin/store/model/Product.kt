package store.model

data class Product(
    val name: String,
    val price: Int,
    var quantity: Int,
    val promotion: Promotion?
)
