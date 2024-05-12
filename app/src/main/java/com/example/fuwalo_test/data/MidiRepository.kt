package com.example.fuwalo_test.data

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call

interface MidiRepository {
    fun transcribeAudio(audioPart: MultipartBody.Part): Call<ResponseBody>
}