package com.vadim.manganal.domain.entity

data class CartItem(
    val product: Product = Product(),
    val quantity: Int = 0
)
data class Cart(
    val items: List<CartItem> = emptyList()
)