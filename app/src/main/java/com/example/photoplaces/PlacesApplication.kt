package com.example.photoplaces

import android.app.Application
import com.example.photoplaces.di.*
import io.realm.Realm
import io.realm.RealmConfiguration
import org.koin.android.ext.android.startKoin

class PlacesApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        initRealm()
        startKoin(this, listOf(connInterceptorModule, apiModule, netModule, networkDataSource, repositoryModule, databaseModule))
    }

    private fun initRealm() {
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("places")
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config);
    }
}