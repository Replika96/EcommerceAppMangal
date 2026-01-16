package com.vadim.manganal.utils

import android.content.Context
import android.net.Uri
import com.vadim.manganal.domain.entity.Product
import java.io.File

object UriUtils {
    fun getFileFromUri(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Не удалось открыть URI")
        val tempFile = File.createTempFile("upload", null, context.cacheDir)
        tempFile.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        return tempFile
    }
    fun getProductById(products: List<Product>, productId: String): Product? {
        return products.find { it.id == productId }
    }
}
