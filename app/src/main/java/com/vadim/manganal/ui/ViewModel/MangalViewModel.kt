package com.vadim.manganal.ui.theme.ViewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.vadim.manganal.data.repository.MangalRepositoryImpl
import com.vadim.manganal.domain.Repository.FavoritesRepository
import com.vadim.manganal.domain.entity.Product
import com.vadim.manganal.domain.Repository.MangalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangalViewModel @Inject constructor(
    private val repository: MangalRepositoryImpl,
    private val firestore: FirebaseFirestore,
    private val favoritesRepository: FavoritesRepository,
) : ViewModel() {

    private val _products = MutableStateFlow<List<Product>>(emptyList()) //"внутренний" источник данных
    val products: StateFlow<List<Product>> = _products
    private val _favorites = MutableStateFlow<Set<String>>(emptySet())
    val favorites: StateFlow<Set<String>> = _favorites

    init {
        loadProducts()

    }

    private fun loadProducts() {
        viewModelScope.launch {
            _products.value = repository.getProducts()
        }
    }

    fun addProduct(product: Product) {
        viewModelScope.launch {
            repository.addProduct(product)
            loadProducts()
        }
    }

    fun editProduct(product: Product, documentId: String) {
        viewModelScope.launch {
            repository.updateProduct(product, documentId)
            loadProducts()
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
            loadProducts()
        }
    }


    private var snapshotListener: ListenerRegistration? = null

    fun startListening() {
        // подписываемся на изменения коллекции "products" в файрсторе
        snapshotListener = firestore.collection("products")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("Firestore", "Listen failed", error)
                    return@addSnapshotListener
                }

                val items = snapshot?.toObjects(Product::class.java) ?: emptyList()
                _products.value = items
            }
    }

    fun stopListening() {
        snapshotListener?.remove()
    }

}
