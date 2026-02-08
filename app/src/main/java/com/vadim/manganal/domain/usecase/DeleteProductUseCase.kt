package com.vadim.manganal.domain.usecase

import com.vadim.manganal.domain.repository.MangalRepository

interface DeleteProductUseCase{
    suspend operator fun invoke(productId: String)
}
class DeleteProductUseCaseImpl(
    private val repository: MangalRepository
): DeleteProductUseCase {
    override suspend operator fun invoke(productId: String){
        if (productId.isBlank()) return
        return repository.deleteProduct(productId)
    }
}