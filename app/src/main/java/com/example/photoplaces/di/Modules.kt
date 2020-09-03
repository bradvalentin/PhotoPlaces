package com.example.photoplaces.di

import android.app.Application
import com.example.photoplaces.data.db.PlacesDao
import com.example.photoplaces.data.db.PlacesDaoImpl
import com.example.photoplaces.data.network.ConnectivityInterceptor
import com.example.photoplaces.data.network.ConnectivityInterceptorImpl
import com.example.photoplaces.data.network.RemoteDataSourceInterface
import com.example.photoplaces.data.network.RemoteDataSourceInterfaceImpl
import com.example.photoplaces.data.network.api.PlacesApiService
import com.example.photoplaces.data.provider.DistanceProvider
import com.example.photoplaces.data.provider.DistanceProviderImpl
import com.example.photoplaces.data.repository.PlacesRepository
import com.example.photoplaces.data.repository.PlacesRepositoryImpl
import com.example.photoplaces.ui.newPlace.NewPlaceFragmentViewModelFactory
import com.example.photoplaces.ui.places.PlacesViewModelFactory
import com.example.photoplaces.utils.Constants
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val API_BASE_URL = "http://demo1042273.mockable.io/"

val connInterceptorModule = module {

    fun provideConnectivityInterceptor(application: Application): ConnectivityInterceptor {
        return ConnectivityInterceptorImpl(application)
    }

    single { provideConnectivityInterceptor(androidApplication()) }
}

val apiModule = module {
    fun providePlacesApi(retrofit: Retrofit): PlacesApiService {
        return retrofit.create(PlacesApiService::class.java)
    }

    single { providePlacesApi(get()) }
}

val netModule = module {

    fun provideHttpClient(connectivityInterceptor: ConnectivityInterceptor): OkHttpClient {
        val requestInterceptor = Interceptor { chain ->

            val url = chain.request()
                .url()
                .newBuilder()
                .build()

            val request = chain.request()
                .newBuilder()
                .url(url)
                .build()

            return@Interceptor chain.proceed(request)
        }

        val okHttpClientBuilder = OkHttpClient.Builder()
            .addInterceptor(requestInterceptor)
            .addInterceptor(connectivityInterceptor)

        return okHttpClientBuilder.build()
    }

    fun provideGson(): Gson {
        return GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()
    }

    fun provideRetrofit(factory: Gson, client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(factory))
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .client(client)
            .build()
    }

    single { provideHttpClient(get()) }
    single { provideGson() }
    single { provideRetrofit(get(), get()) }

}

val networkDataSource = module {
    fun provideNetworkDataSource(placesApiService: PlacesApiService): RemoteDataSourceInterface {
        return RemoteDataSourceInterfaceImpl(placesApiService)
    }

    single { provideNetworkDataSource(get()) }
}

val factoryModule = module {
    fun providePlacesViewModelFactory(placesRepository: PlacesRepository): PlacesViewModelFactory {
        return PlacesViewModelFactory(placesRepository)
    }

    fun provideNewPlaceFragmentViewModelFactory(placesRepository: PlacesRepository): NewPlaceFragmentViewModelFactory {
        return NewPlaceFragmentViewModelFactory(placesRepository)
    }
    factory { providePlacesViewModelFactory(get()) }

    factory { provideNewPlaceFragmentViewModelFactory(get()) }
}

val repositoryModule = module {
    fun providePlacesRepository(
        remoteDataSource: RemoteDataSourceInterface,
        placesDao: PlacesDao
    ): PlacesRepository {
        return PlacesRepositoryImpl(remoteDataSource, placesDao)
    }

    single { providePlacesRepository(get(), get()) }
}

val databaseModule = module {

    fun providePlacesDao(): PlacesDao {
        return PlacesDaoImpl()
    }

    factory { providePlacesDao() }

}

val distanceProviderModule = module {
    fun provideDistanceProvider(): DistanceProvider {
        return DistanceProviderImpl()
    }

    single { provideDistanceProvider() }
}
