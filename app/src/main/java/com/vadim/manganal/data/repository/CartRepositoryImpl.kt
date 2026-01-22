package com.vadim.manganal.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.vadim.manganal.domain.Repository.CartRepository
import com.vadim.manganal.domain.entity.Cart
import com.vadim.manganal.domain.entity.CartItem
import com.vadim.manganal.domain.entity.Product
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CartRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): CartRepository  {
    override val cartsCollection = firestore.collection("carts")

    override suspend fun getCart(userId: String): Flow<Cart?> = callbackFlow {
        if (userId.isEmpty()) {
            trySend(null)
            close() // завершаем поток, если нет идентификатора
            return@callbackFlow
        }

        val docRef = cartsCollection.document(userId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            error?.let {
                Log.e("CartFlow", "Error: ${it.message}")
                trySend(null)
                return@addSnapshotListener
            }
            // пробуем десериализовать корзину
            val cart = snapshot?.takeIf { it.exists() }?.toObject<Cart>()
            // если корзины нет, тогда отправляем пустую корзину
            trySend(cart ?: Cart())
        }

        awaitClose { listener.remove() }
    }

    override suspend fun addToCart(
        userId: String,
        newProduct: Product,
        quantity: Int
    ) {
        val docRef = cartsCollection.document(userId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val currentCart = snapshot.takeIf { it.exists() }?.toObject<Cart>()?: Cart()
            val updatedItems = currentCart.items.toMutableList().apply {
                val existingItemIndex = indexOfFirst { it.product.id == newProduct.id }
                if (existingItemIndex >= 0) {
                    val existingItem = this[existingItemIndex]
                    // обновляем количество товара, если он уже есть
                    this[existingItemIndex] = existingItem.copy(quantity = existingItem.quantity + quantity)
                } else {
                    // если товара нет, добавляем новый элемент корзины
                    add(CartItem(product = newProduct, quantity = quantity))
                }
            }
            // сохраняем обновленную корзину в Firestore
            transaction.set(docRef, Cart(items = updatedItems))
        }.await()
    }

    override suspend fun removeItemFromCart(
        userId: String,
        productId: String
    ) {
        val docRef = cartsCollection.document(userId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            // получаем текущую корзину или создаем пустую, если её нет
            val currentCart = snapshot.takeIf { it.exists() }?.toObject<Cart>() ?: Cart()
            // фильтруем корзину, убирая элемент с заданным productId
            val updatedItems = currentCart.items.filter { it.product.id != productId }
            transaction.set(docRef, Cart(items = updatedItems))
        }.await()
    }
}