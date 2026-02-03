package com.vadim.manganal.domain.entity

import com.vadim.manganal.presentation.theme.screens.SortField
import com.vadim.manganal.presentation.theme.screens.SortOrder

data class SortOption(
    val field: SortField,
    val order: SortOrder
)
