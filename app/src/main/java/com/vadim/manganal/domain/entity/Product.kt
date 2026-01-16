package com.vadim.manganal.domain.entity

data class Product(
    val id: String = "",
    val name: String = "",
    val price: Int = 0,
    val category: String = "",
    val description: String = "",
    val imageUrl: String = ""
)

