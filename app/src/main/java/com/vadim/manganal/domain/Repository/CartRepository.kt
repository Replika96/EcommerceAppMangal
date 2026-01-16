package com.vadim.manganal.domain.Repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.vadim.manganal.domain.entity.Product
import com.vadim.manganal.domain.entity.Cart
import com.vadim.manganal.domain.entity.CartItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CartRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    private val cartsCollection = firestore.collection("carts")

    // получение корзины пользователя в виде Flow
    fun getCart(userId: String): Flow<Cart?> = callbackFlow {
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

    // добавление товара в корзину
    // если товар уже есть, увеличиваем quantity.
    suspend fun addToCart(userId: String, newProduct: Product, quantity: Int = 1) {
        val docRef = cartsCollection.document(userId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            // пробуем получить текущую корзину, если её нет - создаем пустую
            val currentCart = snapshot.takeIf { it.exists() }?.toObject<Cart>() ?: Cart()

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

    // удаление товара из корзины по его productId
    suspend fun removeItemFromCart(userId: String, productId: String) {
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




