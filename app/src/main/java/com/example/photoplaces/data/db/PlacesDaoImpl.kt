package com.example.photoplaces.data.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.photoplaces.data.entity.Place
import com.example.photoplaces.utils.asLiveData
import io.realm.Realm

class PlacesDaoImpl : PlacesDao {

    override fun addAllPlaces(places: List<Place>): Boolean {

        val realm = Realm.getDefaultInstance()

        return try {
            realm.executeTransactionAsync {
                it.insertOrUpdate(places)
            }
            realm.close()
            true
        } catch (e: Exception) {
            println(e)
            false
        }
    }

    override fun fetchAllPlaces(): LiveData<List<Place>> {

        val realm = Realm.getDefaultInstance()
            val realmLiveData = realm.where(Place::class.java).findAllAsync().asLiveData()

            return Transformations.map(realmLiveData) { realmResult ->
                realm.copyFromRealm(realmResult)
            }
    }

    override fun isDatabaseEmpty(): Boolean {
        val realm = Realm.getDefaultInstance()
        val isEmpty = realm.isEmpty
        realm.close()
        return isEmpty
    }

    override fun insertOrUpdatePlace(place: Place): Place? {

        val realm = Realm.getDefaultInstance()

        return try {
            realm.executeTransactionAsync {
                it.copyToRealmOrUpdate(place)
            }
            realm.close()
            place
        } catch (e: Exception) {
            println(e)
            null
        }

    }

}