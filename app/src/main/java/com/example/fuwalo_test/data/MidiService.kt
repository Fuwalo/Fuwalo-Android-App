package com.example.fuwalo_test.data

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MidiService {

    @Multipart
    @POST("transcribe")
    fun transcribeAudio(
        @Part audio: MultipartBody.Part
    ): Call<ResponseBody>

}