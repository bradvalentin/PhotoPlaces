package com.example.photoplaces

import android.app.Application
import com.example.photoplaces.di.*
import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.android.ext.android.startKoin

const val DB_NAME = "places"

class PlacesApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initRealm()
        startKoin(
            this,
            listOf(
                connInterceptorModule,
                apiModule,
                netModule,
                networkDataSource,
                repositoryModule,
                factoryModule,
                databaseModule,
                distanceProviderModule
            )
        )
    }
    private fun initRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name(DB_NAME)
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config);
    }
}