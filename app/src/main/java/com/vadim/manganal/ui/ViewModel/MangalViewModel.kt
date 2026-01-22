package com.vadim.manganal.ui.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vadim.manganal.data.repository.MangalRepositoryImpl
import com.vadim.manganal.domain.entity.Product
import com.vadim.manganal.domain.entity.ProductState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MangalViewModel @Inject constructor(
    private val repository: MangalRepositoryImpl
) : ViewModel() {

     val state: StateFlow<ProductState> =
        repository.observeProducts()
            .map<List<Product>, ProductState>{ products ->
                ProductState.Success(products)
            }
            .onStart { emit(ProductState.Loading) }
            .catch { e ->
                emit(ProductState.Error(e.message?: "Unknowm error"))
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ProductState.Loading
            )


    fun addProduct(product: Product) {
        viewModelScope.launch {
            repository.addProduct(product)
        }
    }

    fun editProduct(product: Product, documentId: String) {
        viewModelScope.launch {
            repository.updateProduct(product, documentId)
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }

}
