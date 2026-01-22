package com.vadim.manganal.data.repository

import android.util.Log
import com.vadim.manganal.data.retrofit.ImgurApiService
import com.vadim.manganal.data.retrofit.ImgurResponse
import com.vadim.manganal.domain.Repository.ImageRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.HttpException
import java.io.File
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val apiService: ImgurApiService,
): ImageRepository {
    override suspend fun uploadImageToImgur(imageFile: File): ImgurResponse? {
        return try {
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, requestFile)

            apiService.uploadImage(imagePart)
        } catch (e: Exception) {
            if (e is HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                Log.e("ImageRepository", "Ошибка HTTP ${e.code()}: $errorBody")
            } else {
                Log.e("ImageRepository", "Ошибка: ${e.message}", e)
            }
            null
        }
    }
}
