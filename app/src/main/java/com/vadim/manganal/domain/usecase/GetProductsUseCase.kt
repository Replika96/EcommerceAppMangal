package com.vadim.manganal.domain.usecase

import com.vadim.manganal.domain.entity.Product
import com.vadim.manganal.domain.repository.MangalRepository
import kotlinx.coroutines.flow.Flow

interface GetProductsUseCase {
    operator fun invoke(): Flow<List<Product>>
}

class GetProductsUseCaseImpl(
    private val repositoryImpl: MangalRepository
) : GetProductsUseCase {
    override fun invoke(): Flow<List<Product>> {
        return repositoryImpl.observeProducts()
    }
}