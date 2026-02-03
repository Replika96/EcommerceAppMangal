package com.vadim.manganal.domain.repository


import com.vadim.manganal.data.retrofit.ImgurResponse
import java.io.File

interface ImageRepository{
    suspend fun uploadImageToImgur(imageFile: File): ImgurResponse?
}

