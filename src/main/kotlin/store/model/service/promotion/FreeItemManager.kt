package store.model.service.promotion

import store.model.entity.OrderItem

class FreeItemManager {
    private val freeItems = mutableListOf<OrderItem>()

    fun addFreeItem(freeItem: OrderItem) {
        // 기존 무료 항목 중 같은 상품이 있는지 확인
        val existingItem = freeItems.find { it.productName == freeItem.productName }
        if (existingItem != null) {
            // 기존에 존재하는 경우 수량 증가
            existingItem.orderQuantity += freeItem.orderQuantity
        } else {
            // 새로운 항목이면 무료 품목 목록에 추가
            freeItems.add(freeItem)
        }
    }

    fun getFreeItems(): List<OrderItem> = freeItems.toList()

    fun initializeFreeItem() {
        freeItems.clear()
    }
}
