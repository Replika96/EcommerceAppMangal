package com.vadim.manganal.domain.Repository

import com.google.firebase.firestore.CollectionReference
import com.vadim.manganal.domain.entity.Product
import kotlinx.coroutines.flow.Flow

interface MangalRepository {
    val productsCollection: CollectionReference

    fun observeProducts(): Flow<List<Product>>
    suspend fun getProductById(productId: String): Product?

    suspend fun addProduct(product: Product)

    suspend fun updateProduct(product: Product,productId: String)

    suspend fun deleteProduct(productId: String)
}

