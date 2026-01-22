package com.vadim.manganal.ui.viewModel

import androidx.lifecycle.ViewModel
import com.vadim.manganal.domain.entity.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeMangalViewModel : ViewModel() {
    val products: StateFlow<List<Product>> = MutableStateFlow(
        listOf(
            Product(
                id = "1",
                name = "Мангал разборный \"Вы Уху Ели?",
                price = 1000,
                imageUrl = "https://i.imgur.com/G9CEjNI.jpeg"
            ),
            Product(
                id = "2",
                name = "Печь-мангал \"Акация\" Про с крышей",
                price = 2000,
                imageUrl = "https://i.imgur.com/Lv7mnqn.jpeg"
            )
        )
    ).asStateFlow()
}