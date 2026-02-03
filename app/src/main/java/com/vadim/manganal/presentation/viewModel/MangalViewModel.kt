package com.vadim.manganal.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vadim.manganal.data.repository.MangalRepositoryImpl
import com.vadim.manganal.domain.entity.Product
import com.vadim.manganal.domain.entity.ProductState
import com.vadim.manganal.domain.usecase.AddProductUseCase
import com.vadim.manganal.domain.usecase.DeleteProductUseCase
import com.vadim.manganal.domain.usecase.GetProductsUseCase
import com.vadim.manganal.domain.usecase.UpdateProductUseCase
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
    private val getProductsUseCase: GetProductsUseCase,
    private val addProductUseCase: AddProductUseCase,
    private val updateProductUseCase: UpdateProductUseCase,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {

     val state: StateFlow<ProductState> =
         getProductsUseCase()
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
            addProductUseCase(product)
        }
    }

    fun editProduct(product: Product, documentId: String) {
        viewModelScope.launch {
            updateProductUseCase(product, documentId)
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            deleteProductUseCase(productId)
        }
    }

}
