package com.example.photoplaces.di

import android.app.Application
import androidx.room.Room
import com.example.photoplaces.data.db.PlacesDao
import com.example.photoplaces.data.db.PlacesDatabase
import com.example.photoplaces.data.network.ConnectivityInterceptor
import com.example.photoplaces.data.network.ConnectivityInterceptorImpl
import com.example.photoplaces.data.network.RemoteDataSourceInterface
import com.example.photoplaces.data.network.RemoteDataSourceInterfaceImpl
import com.example.photoplaces.data.network.api.PlacesApiService
import com.example.photoplaces.data.provider.DistanceProvider
import com.example.photoplaces.data.provider.DistanceProviderImpl
import com.example.photoplaces.data.repository.PlacesRepository
import com.example.photoplaces.data.repository.PlacesRepositoryImpl
import com.example.photoplaces.ui.newPlace.NewPlaceFragmentViewModel
import com.example.photoplaces.ui.places.PlacesViewModel
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val API_BASE_URL = "http://demo1042273.mockable.io/"
const val DB_NAME = "places"

val databaseModule = module {

    fun provideDatabase(application: Application): PlacesDatabase {
        return Room.databaseBuilder(application, PlacesDatabase::class.java, DB_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    fun providePlacesDao(database: PlacesDatabase): PlacesDao {
        return database.placesDao()
    }

    single { provideDatabase(androidApplication()) }
    single { providePlacesDao(get()) }
}

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

val viewModelModule = module {

    viewModel {
        PlacesViewModel(get())
    }

    viewModel {
        NewPlaceFragmentViewModel(get())
    }
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

val distanceProviderModule = module {
    fun provideDistanceProvider(): DistanceProvider {
        return DistanceProviderImpl()
    }

    single { provideDistanceProvider() }
}
