package com.example.photoplaces.ui.newPlace

import androidx.lifecycle.*
import com.example.photoplaces.data.entity.Place
import com.example.photoplaces.data.repository.PlacesRepository
import com.example.photoplaces.utils.safeLet
import kotlinx.coroutines.launch

const val DOT_TEXT = "."
const val MINUS_TEXT = "-"
const val MIN_VALUE_LATITUDE = -90.0
const val MAX_VALUE_LATITUDE = 90.0
const val MIN_VALUE_LONGITUDE = -180.0
const val MAX_VALUE_LONGITUDE = 80.0

class NewPlaceFragmentViewModel(private val placesRepository: PlacesRepository) : ViewModel() {

    var place: Place? = null

    val placeUpdatedOrInsertedLiveData: LiveData<Place?> = MutableLiveData()
    private fun setPlaceUpdatedOrInsertedLiveData(place: Place?) {
        (placeUpdatedOrInsertedLiveData as MutableLiveData).value = place
    }

    val labelLiveData = MutableLiveData<String>()
    val addressLiveData = MutableLiveData<String>()
    val imageUrlLiveData = MutableLiveData<String>()
    val latitudeLiveData = MutableLiveData<String>()
    val longitudeLiveData = MutableLiveData<String>()

    val formMediator = MediatorLiveData<Boolean>().apply {
        addSource(labelLiveData) { validateForm() }
        addSource(addressLiveData) { validateForm() }
        addSource(latitudeLiveData) { validateForm() }
        addSource(longitudeLiveData) { validateForm() }
    }

    val latitudeFieldMediator = MediatorLiveData<Boolean>().apply {
        addSource(latitudeLiveData) { validateLatitudeField() }
    }

    val longitudeFieldMediator = MediatorLiveData<Boolean>().apply {
        addSource(longitudeLiveData) { validateLongitudeField() }
    }

    fun bindPlaceData() {
        labelLiveData.value = place?.label
        longitudeLiveData.value = (place?.lng ?: 0.0).toString()
        latitudeLiveData.value = (place?.lat ?: 0.0).toString()
        addressLiveData.value = place?.address
        imageUrlLiveData.value = place?.image
    }

    private fun validateLongitudeField() {
        longitudeLiveData.value?.let { longitude ->
            longitudeFieldMediator.value =
                checkCoordinateFormat(longitude, MIN_VALUE_LONGITUDE, MAX_VALUE_LONGITUDE)


        } ?: run {
            longitudeFieldMediator.value = false
        }
    }

    private fun checkCoordinateFormat(
        coordinate: String,
        minValue: Double,
        maxValue: Double
    ): Boolean {
        return !coordinate.startsWith(DOT_TEXT) &&
                coordinate != MINUS_TEXT &&
                coordinate.isNotEmpty() &&
                (coordinate.toDouble() in minValue..maxValue)
    }

    private fun validateLatitudeField() {
        latitudeLiveData.value?.let { latitude ->
            latitudeFieldMediator.value =
                checkCoordinateFormat(latitude, MIN_VALUE_LATITUDE, MAX_VALUE_LATITUDE)

        } ?: run {
            latitudeFieldMediator.value = false
        }
    }

    private fun validateForm() {

        safeLet(
            addressLiveData.value,
            labelLiveData.value,
            latitudeLiveData.value,
            longitudeLiveData.value
        ) { adr, lbl, latitude, longitude ->
            formMediator.value =
                lbl.isNotEmpty() &&
                        adr.isNotEmpty() &&
                        checkCoordinateFormat(
                            longitude,
                            MIN_VALUE_LONGITUDE,
                            MAX_VALUE_LONGITUDE
                        ) &&
                        checkCoordinateFormat(latitude, MIN_VALUE_LATITUDE, MAX_VALUE_LATITUDE)

        } ?: run {
            formMediator.value = false
        }

    }

    fun insertOrUpdate() {

        safeLet(
            addressLiveData.value,
            labelLiveData.value,
            latitudeLiveData.value,
            longitudeLiveData.value
        ) { adr, lbl, latitude, longitude ->
            val newPlace = Place()
            newPlace.apply {
                place?.id?.let {
                    id = it
                }
                address = adr
                image = imageUrlLiveData.value
                label = lbl
                lat = latitude.toDouble()
                lng = longitude.toDouble()

                viewModelScope.launch {
                    setPlaceUpdatedOrInsertedLiveData(placesRepository.insertOrUpdatePlace(newPlace))
                }
            }
        } ?: run {
            setPlaceUpdatedOrInsertedLiveData(null)
        }

    }

    override fun onCleared() {
        formMediator.apply {
            removeSource(labelLiveData)
            removeSource(addressLiveData)
            removeSource(latitudeLiveData)
            removeSource(longitudeLiveData)
        }
        latitudeFieldMediator.removeSource(latitudeLiveData)
        longitudeFieldMediator.removeSource(longitudeLiveData)
    }

}