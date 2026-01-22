package com.vadim.manganal.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vadim.manganal.data.repository.MangalRepositoryImpl
import com.vadim.manganal.domain.entity.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: MangalRepositoryImpl
) : ViewModel() {
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _product.value = repository.getProductById(productId)
        }
    }
}