package com.example.photoplaces.ui.places

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import com.example.photoplaces.data.provider.DistanceProvider
import com.example.photoplaces.data.provider.LocationViewModel
import com.example.photoplaces.ui.newPlace.NewPlaceFragment
import com.example.photoplaces.ui.placeDetails.PlaceDetailsActivity
import com.example.photoplaces.utils.CarouselSnapHelper
import com.example.photoplaces.utils.Constants.ACTIVITY_REQUEST_CODE
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

    private val distanceProvider: DistanceProvider by inject()

    private val placesAdapter: PlacesListAdapter by lazy {
        PlacesListAdapter(this)
    }
    private val snapHelper: CarouselSnapHelper by lazy { CarouselSnapHelper() }
    private val skeleton: Skeleton by lazy {
        placesRecyclerView.applySkeleton(
            R.layout.place_list_item,
            10
        )
    }

    private var position: Int = -1

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

        addNewPlaceButton.setOnClickListener {
            showDialog()
        }

    }

    private fun calculateDistance(location: CurrentLocation) {

        placesAdapter.places.forEachIndexed { index, place ->
            val distance = distanceProvider.distanceBetweenTwoLocations(place, location)
            place.distance = distance.formatToKm(2)
            placesAdapter.notifyItemChanged(index, Unit)
        }
    }

    private fun bindLocation() {

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

    private fun bindUI() {
        GlobalScope.launch(Dispatchers.Main) {
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

    }

    private fun updatePlacesList(location: List<Place>) {
        placesAdapter.setDataSource(location)
        skeleton.showOriginal()
    }

    override fun placeItemPressed(place: Place, position: Int) {
        val intent = Intent(this, PlaceDetailsActivity::class.java)
        intent.putExtra("place", place)
        startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
        overridePendingTransition(0, R.anim.fade_out)
        this.position = position
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = data?.getParcelableExtra<Place>("newPlace")
                place?.let {
                    placesAdapter.places[position] = place
                    placesAdapter.notifyItemChanged(position)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    private fun showDialog() {
        val currentFragment =
            NewPlaceFragment()
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, 0, 0, android.R.anim.fade_out)
            .replace(R.id.fragmentHolder, currentFragment)
            .addToBackStack("newPlaceFragment")
            .commit()
    }

}