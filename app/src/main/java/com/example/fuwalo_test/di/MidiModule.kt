package com.example.fuwalo_test.di

import com.example.fuwalo_test.data.MidiRepository
import com.example.fuwalo_test.data.MidiRepositoryImp
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class MidiModule {


    @Binds
    abstract fun bindMidiRepository(
        midiRepositoryImp: MidiRepositoryImp
    ): MidiRepository

}