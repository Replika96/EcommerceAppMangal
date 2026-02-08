package com.vadim.manganal.domain.usecase


import com.vadim.manganal.domain.entity.Product
import com.vadim.manganal.domain.repository.MangalRepository

interface AddProductUseCase{
    suspend operator fun invoke(product: Product)
}
class AddProductUseCaseImpl(
    private val repositoryImpl: MangalRepository
): AddProductUseCase {
    override suspend operator fun invoke(product: Product){
        val normalized = product.copy(
            name = product.name.trim().replaceFirstChar { it.uppercase() },
            description = product.description.trim()
        )
        return repositoryImpl.addProduct(normalized)
    }
}