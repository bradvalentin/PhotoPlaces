package com.example.photoplaces

import android.app.Application
import com.example.photoplaces.di.*

import org.koin.android.ext.android.startKoin

class PlacesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin(
            this,
            listOf(
                connInterceptorModule,
                apiModule,
                netModule,
                networkDataSource,
                repositoryModule,
                databaseModule,
                distanceProviderModule,
                viewModelModule
            )
        )
    }

}