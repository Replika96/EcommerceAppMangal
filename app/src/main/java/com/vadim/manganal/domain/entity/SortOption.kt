package com.vadim.manganal.domain.entity

import com.vadim.manganal.ui.theme.screens.SortField
import com.vadim.manganal.ui.theme.screens.SortOrder

data class SortOption(
    val field: SortField,
    val order: SortOrder
)
