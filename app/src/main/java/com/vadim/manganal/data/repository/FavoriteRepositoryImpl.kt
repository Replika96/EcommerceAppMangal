package com.vadim.manganal.data.repository

import com.vadim.manganal.domain.Repository.FavoriteRepository
import javax.inject.Inject
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FavoriteRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): FavoriteRepository {
    override val collection = firestore.collection("favorites")

    override suspend fun getFavorites(userId: String): Flow<List<String>> = callbackFlow {
        if (userId.isEmpty()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val docRef = collection.document(userId)
        val listener = docRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }

            val items = snapshot?.get("items") as? List<*> ?: emptyList<Any>()
            trySend(items.filterIsInstance<String>())
        }

        awaitClose { listener.remove() }
    }

    override suspend fun addToFavorites(userId: String, productId: String) {
        val docRef = collection.document(userId)
        firestore.runTransaction { tx ->
            val snapshot = tx.get(docRef)
            val current = if (snapshot.exists()) {
                snapshot.get("items") as? List<*> ?: emptyList<String>()
            } else {
                emptyList<String>()
            }
            val updated = current.filterIsInstance<String>().toMutableSet().apply {
                add(productId)
            }.toList()
            tx.set(docRef, mapOf("items" to updated))
        }.await()
    }

    override suspend fun removeFromFavorites(userId: String, productId: String) {
        val docRef = collection.document(userId)
        firestore.runTransaction { tx ->
            val snapshot = tx.get(docRef)
            val current = snapshot.get("items") as? List<*> ?: emptyList<String>()
            val updated = current.filterIsInstance<String>().filter { it != productId }
            tx.set(docRef, mapOf("items" to updated))
        }.await()
    }
}