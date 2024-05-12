package com.example.fuwalo_test.ui

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.fuwalo_test.data.MidiRepository
import com.example.fuwalo_test.data.TranscriptionResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import org.apache.commons.io.FileUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

@HiltViewModel
class MidiViewModel @Inject constructor(private val midiRepository: MidiRepository) : ViewModel() {

    private val viewModelScope = CoroutineScope(Dispatchers.IO)

    private val _uiStateFlow = MutableStateFlow<UiState>(UiState.IDLE)
    val uiStateFlow: StateFlow<UiState> = _uiStateFlow.asStateFlow()

    private val _userFlow = MutableStateFlow<Uri?>(null)
    val userFlow: StateFlow<Uri?> = _userFlow.asStateFlow()
    fun updateUri(uri: Uri) {
        _userFlow.value = uri
    }

    private val _transcriptionResponse = MutableLiveData<TranscriptionResponse>()
    val transcriptionResponse: LiveData<TranscriptionResponse>
        get() = _transcriptionResponse

    private fun createFileFromUri(context: Context, name: String, uri: Uri): File? {
        return try {
            val stream = context.contentResolver.openInputStream(uri)
            val file =
                File.createTempFile(
                    "${name}_${System.currentTimeMillis()}",
                    ".wav",
                    context.cacheDir
                )
            FileUtils.copyInputStreamToFile(
                stream,
                file
            )  // Use this one import org.apache.commons.io.FileUtils
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun transcribeAudio(context: Context, audioUri: Uri, name: String) {

        _uiStateFlow.value = UiState.LOADING
        viewModelScope.launch {
            val audioFile = createFileFromUri(context, name, audioUri)
            audioFile?.let {
                val audioRequestBody = audioFile.asRequestBody("audio/wav".toMediaTypeOrNull())
                val audioPart =
                    MultipartBody.Part.createFormData("audio", audioFile.name, audioRequestBody)

                midiRepository.transcribeAudio(audioPart)
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            if (response.isSuccessful) {

                                viewModelScope.launch {
                                    val responseBody = response.body()
                                    responseBody?.let { saveResponseBodyToFile(it , context, audioFile.name) }
                                }


                            } else {
                                // Handle unsuccessful response
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            // Handle network errors
                            _uiStateFlow.value = UiState.ERROR(t.message ?: "")
                        }
                    })
            }
        }

    }

    private suspend fun saveResponseBodyToFile(responseBody: ResponseBody, context: Context, name: String) {
        val file = File.createTempFile(
            name,
            ".midi",
            context.cacheDir
        )
        val inputStream = responseBody.byteStream()
        val outputStream = FileOutputStream(file)
        val buffer = ByteArray(4096)
        var bytesRead: Int
        while (inputStream.read(buffer).also { bytesRead = it } != -1) {
            outputStream.write(buffer, 0, bytesRead)
        }
        outputStream.close()
        inputStream.close()
        _uiStateFlow.emit(UiState.DONE(file))
    }

    sealed class UiState{
        data object IDLE: UiState()
        data object LOADING: UiState()
        data class ERROR(val e: String) : UiState()
        data class DONE(val file:File) : UiState()
    }

}