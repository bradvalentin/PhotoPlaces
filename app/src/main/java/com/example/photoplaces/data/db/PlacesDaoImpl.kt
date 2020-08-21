package com.example.photoplaces.data.db

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.photoplaces.data.entity.Place
import com.example.photoplaces.utils.asLiveData
import io.realm.Realm

class PlacesDaoImpl : PlacesDao {

    var num = 0
    private fun setUniqueId(): Int {
        num += 1
        return num
    }

    override fun addAllPlaces(places: List<Place>): Boolean {
        return try {
            places.forEach {
                it.id = setUniqueId()
            }
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
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
        val realmLiveData  = realm.where(Place::class.java).findAllAsync().asLiveData()

        return Transformations.map(realmLiveData) { realmResult ->
            val placesCopy = realm.copyFromRealm(realmResult)
            realm.close()
            placesCopy
        }
    }

    override fun isDatabaseEmpty(): Boolean {
        val realm = Realm.getDefaultInstance()
        return realm.isEmpty
    }

}