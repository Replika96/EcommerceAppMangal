package com.vadim.manganal.di

import com.vadim.manganal.data.repository.MangalRepositoryImpl
import com.vadim.manganal.domain.repository.MangalRepository
import com.vadim.manganal.domain.usecase.AddProductUseCase
import com.vadim.manganal.domain.usecase.AddProductUseCaseImpl
import com.vadim.manganal.domain.usecase.DeleteProductUseCase
import com.vadim.manganal.domain.usecase.DeleteProductUseCaseImpl
import com.vadim.manganal.domain.usecase.GetProductsUseCase
import com.vadim.manganal.domain.usecase.GetProductsUseCaseImpl
import com.vadim.manganal.domain.usecase.UpdateProductUseCase
import com.vadim.manganal.domain.usecase.UpdateProductUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideAddProductUseCase(
        repository: MangalRepository
    ): AddProductUseCase {
        return AddProductUseCaseImpl(repository)
    }
    @Provides
    @Singleton
    fun provideUpdateProductUseCase(
        repository: MangalRepository
    ): UpdateProductUseCase {
        return UpdateProductUseCaseImpl(repository)
    }
    @Provides
    @Singleton
    fun provideDeleteProductUseCase(
        repository: MangalRepository
    ): DeleteProductUseCase {
        return DeleteProductUseCaseImpl(repository)
    }

    @Provides
    @Singleton
    fun provideGetProductsUseCase(
        repository: MangalRepository
    ): GetProductsUseCase {
        return GetProductsUseCaseImpl(repository)
    }
}