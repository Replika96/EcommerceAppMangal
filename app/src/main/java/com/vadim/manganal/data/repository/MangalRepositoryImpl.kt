package com.vadim.manganal.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.vadim.manganal.domain.Repository.MangalRepository
import com.vadim.manganal.domain.entity.Product
import jakarta.inject.Inject
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


class MangalRepositoryImpl @Inject constructor(
    db: FirebaseFirestore
): MangalRepository{
    override val productsCollection = db.collection("products")
    override fun observeProducts(): Flow<List<Product>> = callbackFlow {
        val listener = productsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val products = snapshot
                    ?.toObjects(Product::class.java)
                    ?: emptyList()

                trySend(products)
            }

        awaitClose {
            listener.remove()
        }
    }

    override suspend fun getProductById(productId: String): Product? {
        return try {
            val document = productsCollection.document(productId).get().await()
            if(document.exists()){
                document.toObject(Product::class.java)
            } else{
                null
            }
        } catch (e:Exception){
            Log.e("ProductRepository", "Ошибка загрузки товара ID: $productId", e)
            null
        }
    }
    override suspend fun addProduct(product: Product) {
        try {
            val newDocRef = productsCollection.document()
            val productWithId = product.copy(id = newDocRef.id)
            newDocRef.set(productWithId).await()
        } catch (e: Exception) {
            Log.d("ProductRepository", "Ошибка добавления: ${e.message}")
        }
    }



    override suspend fun updateProduct(product: Product, productId: String) {
        try {
            productsCollection.document(productId).set(product).await()
        } catch (e: Exception) {
            Log.d("ProductRepository","Ошибка обновления")
        }
    }

    override suspend fun deleteProduct(productId: String) {
        try {
            productsCollection.document(productId).delete().await()
        } catch (e: Exception) {
            Log.d("ProductRepository","Ошибка удаления")
        }
    }
}