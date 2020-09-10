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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.coroutineScope
import com.example.photoplaces.R
import com.example.photoplaces.data.entity.CurrentLocation
import com.example.photoplaces.data.entity.Place
import com.example.photoplaces.data.provider.DistanceProvider
import com.example.photoplaces.data.provider.LocationViewModel
import com.example.photoplaces.databinding.ActivityPlaceDetailsBinding
import com.example.photoplaces.databinding.ActivityPlacesListBinding
import com.example.photoplaces.ui.newPlace.NewPlaceFragment
import com.example.photoplaces.ui.placeDetails.PlaceDetailsActivity
import com.example.photoplaces.utils.CarouselSnapHelper
import com.example.photoplaces.utils.Constants.NEW_PLACE_FRAGMENT_TAG
import com.example.photoplaces.utils.Constants.PARCELABLE_CHANGED_PLACE_KEY
import com.example.photoplaces.utils.Constants.PARCELABLE_PLACE_KEY
import com.example.photoplaces.utils.formatToKm
import com.example.photoplaces.utils.safeClick
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import kotlinx.android.synthetic.main.activity_places_list.*
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

const val ACTIVITY_REQUEST_CODE = 100
const val PERMISSION_RESULT_CODE = 1
const val FLOAT_DECIMALS = 2
const val SKELETON_VIEWS_COUNT = 10
const val INITIAL_POSITION = -1

class PlacesListActivity : AppCompatActivity(), PlaceItemClickListener, PlacesActivityButtonsActions{

    private var position: Int = INITIAL_POSITION


    private val viewModel: PlacesViewModel by viewModel()
    private val locationViewModel: LocationViewModel by lazy {
        ViewModelProviders.of(this).get(LocationViewModel::class.java)
    }

    private val distanceProvider: DistanceProvider by inject()

    private val placesAdapter: PlacesListAdapter by lazy {
        PlacesListAdapter(this)
    }
    private val snapHelper: CarouselSnapHelper by lazy { CarouselSnapHelper() }
    private val skeleton: Skeleton by lazy {
        placesRecyclerView.applySkeleton(
            R.layout.place_list_item,
            SKELETON_VIEWS_COUNT
        )
    }
    private lateinit var binding: ActivityPlacesListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_places_list)

        setSupportActionBar(toolbar)
        binding.listener = this

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

        placesAdapter.places.forEachIndexed { index, place ->
            val distance = distanceProvider.distanceBetweenTwoLocations(place, location)
            place.distance = distance.formatToKm(FLOAT_DECIMALS)
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
        lifecycle.coroutineScope.launch {

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
        val intent = Intent(this, PlaceDetailsActivity::class.java).apply {
            putExtra(PARCELABLE_PLACE_KEY, place)
        }
        startActivityForResult(intent, ACTIVITY_REQUEST_CODE)
        overridePendingTransition(0, R.anim.fade_out)
        this.position = position
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                data?.getParcelableExtra<Place>(PARCELABLE_CHANGED_PLACE_KEY)?.let {
                    placesAdapter.apply {
                        places[position] = it
                        notifyItemChanged(position)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onAddNewPlaceButtonPressed() {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, 0, 0, android.R.anim.fade_out)
            .replace(R.id.fragmentHolder, NewPlaceFragment())
            .addToBackStack(NEW_PLACE_FRAGMENT_TAG)
            .commit()
    }

}


interface PlacesActivityButtonsActions {
    fun onAddNewPlaceButtonPressed()
}