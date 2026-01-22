package com.vadim.manganal.domain.Repository

import com.google.firebase.firestore.CollectionReference
import com.vadim.manganal.domain.entity.Product
import com.vadim.manganal.domain.entity.Cart
import kotlinx.coroutines.flow.Flow

interface CartRepository{
    val cartsCollection: CollectionReference
    suspend fun getCart(userId: String): Flow<Cart?>
    suspend fun addToCart(userId: String, newProduct: Product, quantity: Int)

    suspend fun removeItemFromCart(userId: String, productId: String)
}




