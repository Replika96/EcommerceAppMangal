package com.vadim.manganal.ui.theme.ViewModel

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.ViewModel
import com.vadim.manganal.domain.Repository.ImageRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.io.File


@HiltViewModel
class ImageViewModel @Inject constructor(
    private val repository: ImageRepository
) : ViewModel() {

    private val _uploadStatus = MutableLiveData<String?>()
    val uploadStatus: LiveData<String?> get() = _uploadStatus

    private val _uploadedImageUrl = MutableLiveData<String?>()
    val uploadedImageUrl: LiveData<String?> get() = _uploadedImageUrl

    fun uploadImage(file: File) {
        viewModelScope.launch {
            _uploadStatus.value = "Загрузка изображения..."
            try {
                val response = repository.uploadImageToImgur(file)
                if (response != null && response.success) {
                    _uploadStatus.value = "Изображение успешно загружено!"
                    _uploadedImageUrl.value = response.data.link
                } else {
                    _uploadStatus.value = "Ошибка при загрузке изображения"
                }
            } catch (e: Exception) {
                _uploadStatus.value = "Ошибка: ${e.message}"
                Log.e("ImageViewModel", "Ошибка при загрузке изображения: ${e.message}")
            }
        }
    }

    fun resetUploadStatus() {
        _uploadStatus.value = null
    }

    fun resetUploadedImageUrl() {
        _uploadedImageUrl.value = null
    }
}

