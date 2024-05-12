package com.example.fuwalo_test.data

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import javax.inject.Inject

class MidiRepositoryImp @Inject constructor(
    private val midiService: MidiService
): MidiRepository {


    override fun transcribeAudio(audioPart: MultipartBody.Part): Call<ResponseBody> {
        return midiService.transcribeAudio(audioPart)
    }

}