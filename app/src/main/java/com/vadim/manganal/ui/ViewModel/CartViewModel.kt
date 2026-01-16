package com.vadim.manganal.ui.theme.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vadim.manganal.domain.Repository.CartRepository
import com.vadim.manganal.domain.Repository.RegistrationRepository
import com.vadim.manganal.domain.entity.Cart
import com.vadim.manganal.domain.entity.CartItem
import com.vadim.manganal.domain.entity.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepo: CartRepository,
    private val authRepo: RegistrationRepository
) : ViewModel() {

    /** текущая корзина (null → пользователь не авторизован) */
    @OptIn(ExperimentalCoroutinesApi::class)
    val cart: StateFlow<Cart?> = authRepo.authState              // Flow<FirebaseUser?>
        .map { it?.uid.orEmpty() }                               // uid либо ""
        .distinctUntilChanged()
        .flatMapLatest { uid ->
            if (uid.isBlank()) flowOf(null)                      // не авторизован → null
            else cartRepo.getCart(uid)                           // Flow<Cart?>
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)



    fun addItem(product: Product, qty: Int = 1) =
        launchWithUid { uid -> cartRepo.addToCart(uid, product, qty) }

    fun removeItem(item: CartItem) =
        launchWithUid { uid -> cartRepo.removeItemFromCart(uid, item.product.id) }

    fun increaseItemQuantity(item: CartItem) =
        addItem(item.product, 1)

    fun decreaseItemQuantity(item: CartItem) = launchWithUid { uid ->
        if (item.quantity > 1) cartRepo.addToCart(uid, item.product, -1)
        else                   cartRepo.removeItemFromCart(uid, item.product.id)
    }


    private inline fun launchWithUid(crossinline block: suspend (String) -> Unit) {
        val uid = authRepo.getCurrentUserId() ?: return         // пользователь не авторизован
        viewModelScope.launch { block(uid) }
    }
}
