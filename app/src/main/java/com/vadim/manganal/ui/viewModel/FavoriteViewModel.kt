package com.vadim.manganal.ui.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vadim.manganal.data.repository.FavoriteRepositoryImpl
import com.vadim.manganal.domain.Repository.RegistrationRepository
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
class FavoritesViewModel @Inject constructor(
    private val repo: FavoriteRepositoryImpl,
    private val authRepo: RegistrationRepository
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val favorites: StateFlow<List<String>> = authRepo.authState
        .map { it?.uid.orEmpty() }
        .distinctUntilChanged()
        .flatMapLatest { uid ->
            if (uid.isBlank()) flowOf(emptyList())
            else repo.getFavorites(uid)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private inline fun launchWithUid(crossinline block: suspend (String) -> Unit) {
        val uid = authRepo.getCurrentUserId() ?: return
        viewModelScope.launch { block(uid) }
    }

    fun remove(productId: String) = launchWithUid { repo.removeFromFavorites(it, productId) }

    fun toggle(productId: String) = viewModelScope.launch {
        val uid = authRepo.getCurrentUserId() ?: return@launch
        val current = favorites.value
        if (productId in current) repo.removeFromFavorites(uid, productId)
        else repo.addToFavorites(uid, productId)
    }
}
