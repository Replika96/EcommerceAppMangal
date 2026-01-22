package com.vadim.manganal.domain.entity

sealed interface ProductState {
    object Loading: ProductState
    data class Success(val products: List<Product>): ProductState
    data class Error(val message: String): ProductState
}