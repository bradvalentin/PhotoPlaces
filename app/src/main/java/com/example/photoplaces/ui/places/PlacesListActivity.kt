package com.example.photoplaces.ui.places

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.photoplaces.R
import com.example.photoplaces.data.entity.CurrentLocation
import com.example.photoplaces.data.entity.Place
import com.example.photoplaces.data.provider.LocationViewModel
import com.example.photoplaces.utils.CarouselSnapHelper
import com.example.photoplaces.utils.Constants.PERMISSION_RESULT_CODE
import com.example.photoplaces.utils.formatToKm
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import kotlinx.android.synthetic.main.activity_places_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject


class PlacesListActivity : AppCompatActivity(), PlaceItemClickListener {

    private val viewModelFactory: PlacesViewModelFactory by inject()
    private lateinit var viewModel: PlacesViewModel
    private lateinit var locationViewModel: LocationViewModel

    private val placesAdapter: PlacesListAdapter by lazy {
        PlacesListAdapter(this, places)
    }
    private val snapHelper: CarouselSnapHelper by lazy { CarouselSnapHelper() }
    private val skeleton: Skeleton by lazy {
        placesRecyclerView.applySkeleton(
            R.layout.place_list_item,
            10
        )
    }

    private val places: ArrayList<Place> by lazy { arrayListOf<Place>() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places_list)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProvider(this, viewModelFactory).get(PlacesViewModel::class.java)
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)

        placesRecyclerView.adapter = placesAdapter

        skeleton.showSkeleton()
        snapHelper.attachToRecyclerView(placesRecyclerView)

        bindUI()

        if (hasLocationPermission()) {
            bindLocation()
        } else {
            requestLocationPermission()
        }

    }

    private fun calculateDistance(location: CurrentLocation) {

        places.forEachIndexed { index, place ->
            val locationA = Location("pointA")
            locationA.apply {
                latitude = place.lat
                longitude = place.lng
            }
            val locationB = Location("pointB")
            locationB.apply {
                latitude = location.latitude
                longitude = location.longitude
            }
            val distance = locationA.distanceTo(locationB)
            place.distance = distance.formatToKm(2)
            placesAdapter.notifyItemChanged(index, Unit)

        }
    }

    private fun bindLocation() = GlobalScope.launch(Dispatchers.Main) {

        locationViewModel.locationData.observe(this@PlacesListActivity, Observer {
            calculateDistance(it)
        })

    }

    private fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_RESULT_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode == PERMISSION_RESULT_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                bindLocation()
            } else {
                Toast.makeText(this, getString(R.string.activate_location_text), Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    private fun bindUI() = GlobalScope.launch(Dispatchers.Main) {

        viewModel.getAllPlaces().observe(this@PlacesListActivity, Observer { location ->
            if (location == null || location.isEmpty())
                return@Observer

            updatePlacesList(location)

        })

        viewModel.getDownloadingStatus().observe(this@PlacesListActivity, Observer { status ->
            if (!status)
                Toast.makeText(
                    this@PlacesListActivity,
                    getString(R.string.no_internet_text),
                    Toast.LENGTH_LONG
                ).show()
        })

    }

    private fun updatePlacesList(location: List<Place>) {
        placesAdapter.setDataSource(location)
        skeleton.showOriginal()
    }

    override fun placeItemPressed(place: Place) {
        Toast.makeText(this, place.address, Toast.LENGTH_SHORT).show()
    }

}