package com.vadim.manganal.domain.Repository

import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    val collection: CollectionReference
    suspend fun getFavorites(userId: String): Flow<List<String>>
    suspend fun addToFavorites(userId: String, productId: String)
    suspend fun removeFromFavorites(userId: String, productId: String)
}

