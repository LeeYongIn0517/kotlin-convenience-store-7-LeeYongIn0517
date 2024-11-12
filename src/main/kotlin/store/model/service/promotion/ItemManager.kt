package store.model.service.promotion

import store.model.entity.OrderItem

class ItemManager {
    private val items = mutableListOf<OrderItem>()

    fun addItem(item: OrderItem) {
        val existingItem = items.find { it.productName == item.productName }
        if (existingItem != null) {
            existingItem.orderQuantity += item.orderQuantity
        } else {
            items.add(item)
        }
    }

    fun getItems(): List<OrderItem> = items.toList()

    fun initializeFreeItem() {
        items.clear()
    }
}
