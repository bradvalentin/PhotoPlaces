package com.example.photoplaces.ui.newPlace

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photoplaces.data.entity.Place
import com.example.photoplaces.data.repository.PlacesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NewPlaceFragmentViewModel(private val placesRepository: PlacesRepository) : ViewModel() {

    private val _placeUpdatedOrInsertedLiveData = MutableLiveData<Place?>()
    val placeUpdatedOrInsertedLiveData: LiveData<Place?>
        get() = _placeUpdatedOrInsertedLiveData

    val labelLiveData = MutableLiveData<String>()
    val addressLiveData = MutableLiveData<String>()
    val imageUrlLiveData = MutableLiveData<String>()
    val latitudeLiveData = MutableLiveData<String>()
    val longitudeLiveData = MutableLiveData<String>()


    val formMediator = MediatorLiveData<Boolean>()
    val latitudeFieldMediator = MediatorLiveData<Boolean>()
    val longitudeFieldMediator = MediatorLiveData<Boolean>()

    init {
        formMediator.addSource(labelLiveData) { validateForm() }
        formMediator.addSource(addressLiveData) { validateForm() }
        formMediator.addSource(latitudeLiveData) { validateForm() }
        formMediator.addSource(longitudeLiveData) { validateForm() }

        latitudeFieldMediator.addSource(latitudeLiveData) { validateLatitudeField() }
        longitudeFieldMediator.addSource(longitudeLiveData) { validateLongitudeField() }
    }

    private fun validateLongitudeField() {
        longitudeLiveData.value?.let { longitude ->
            longitudeFieldMediator.value = checkLongitudeFormat(longitude)


        } ?: run {
            longitudeFieldMediator.value = false
        }
    }

    private fun checkLongitudeFormat(longitude: String): Boolean {
        return !longitude.startsWith(".") &&
                longitude != "-" &&
                longitude.isNotEmpty() &&
                (longitude.toDouble() >= -180.0 && longitude.toDouble() <= 80.0)
    }

    private fun validateLatitudeField() {
        latitudeLiveData.value?.let { latitude ->
            latitudeFieldMediator.value = checkLatitudeFormat(latitude)


        } ?: run {
            latitudeFieldMediator.value = false
        }
    }

    private fun checkLatitudeFormat(latitude: String): Boolean {
        return latitude.isNotEmpty() &&
                !latitude.startsWith(".") &&
                latitude != "-" &&
                (latitude.toDouble() >= -90.0 && latitude.toDouble() <= 90.0)
    }

    private fun validateForm() {

        labelLiveData.value?.let { label ->
            addressLiveData.value?.let { address ->
                latitudeLiveData.value?.let { latitude ->
                    longitudeLiveData.value?.let { longitude ->
                        formMediator.value =
                            label.isNotEmpty() &&
                                    address.isNotEmpty() &&
                                    checkLongitudeFormat(longitude) &&
                                    checkLatitudeFormat(latitude)
                    }
                }
            }
        } ?: run {
            formMediator.value = false
        }
    }

    fun insertOrUpdate(placeId: String?) {

        addressLiveData.value?.let { address ->
            labelLiveData.value?.let { label ->
                latitudeLiveData.value?.let { latitude ->
                    longitudeLiveData.value?.let { longitude ->
                        val place = Place()
                        place.apply {
                            placeId?.let {
                                id = it
                            }
                            this.address = address
                            image = imageUrlLiveData.value
                            this.label = label
                            lat = latitude.toDouble()
                            lng = longitude.toDouble()
                            GlobalScope.launch(Dispatchers.Main) {
                                _placeUpdatedOrInsertedLiveData.value =
                                    placesRepository.insertOrUpdatePlace(place)
                            }
                        }
                    } ?: run {
                        _placeUpdatedOrInsertedLiveData.value = null
                    }
                } ?: run {
                    _placeUpdatedOrInsertedLiveData.value = null
                }
            } ?: run {
                _placeUpdatedOrInsertedLiveData.value = null
            }
        } ?: run {
            _placeUpdatedOrInsertedLiveData.value = null
        }

    }

    override fun onCleared() {
        formMediator.removeSource(labelLiveData)
        formMediator.removeSource(addressLiveData)
        formMediator.removeSource(latitudeLiveData)
        formMediator.removeSource(longitudeLiveData)

        latitudeFieldMediator.removeSource(latitudeLiveData)
        longitudeFieldMediator.removeSource(longitudeLiveData)
    }

}