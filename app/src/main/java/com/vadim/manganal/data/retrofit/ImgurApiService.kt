package com.vadim.manganal.data.retrofit

import okhttp3.MultipartBody
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ImgurApiService {
    @Multipart
    @POST("image")
    @Headers("Authorization: Client-ID ae061b5db48e23fY") // увы
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): ImgurResponse
}