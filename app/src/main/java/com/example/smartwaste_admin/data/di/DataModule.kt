package com.example.smartwaste_admin.data.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DataModule {


    @Singleton
    @Provides
    fun provideFirebaseFireStore() : FirebaseFirestore{
        return FirebaseFirestore.getInstance()
    }



    @Provides
    @Singleton
    fun provideContext(
        @ApplicationContext context: Context
    ): Context = context
}