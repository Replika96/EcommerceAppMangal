package com.vadim.manganal.domain.usecase


import com.vadim.manganal.domain.entity.Product
import com.vadim.manganal.domain.repository.MangalRepository


interface UpdateProductUseCase{
    suspend operator fun invoke(product: Product, documentId: String)
}
class UpdateProductUseCaseImpl(
    private val repositoryImpl: MangalRepository
): UpdateProductUseCase {
    override suspend operator fun invoke(
        product: Product,
        documentId: String
    ) {
        return repositoryImpl.updateProduct(product, documentId)
    }

}