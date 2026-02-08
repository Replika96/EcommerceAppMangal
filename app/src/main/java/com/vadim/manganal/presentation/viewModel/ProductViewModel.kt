package com.vadim.manganal.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vadim.manganal.domain.entity.Product
import com.vadim.manganal.domain.repository.MangalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: MangalRepository
) : ViewModel() {
    private val _product = MutableStateFlow<Product?>(null)
    val product: StateFlow<Product?> = _product

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _product.value = repository.getProductById(productId)
        }
    }
}