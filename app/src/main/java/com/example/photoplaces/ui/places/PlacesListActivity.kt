package com.example.photoplaces.ui.places

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.photoplaces.R
import com.example.photoplaces.data.entity.Place
import com.example.photoplaces.utils.CarouselSnapHelper
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

    private val placesAdapter: PlacesListAdapter by lazy {
        PlacesListAdapter(this, places)
    }
    private val snapHelper: CarouselSnapHelper by lazy {CarouselSnapHelper()}
    private val skeleton: Skeleton by lazy {placesRecyclerView.applySkeleton(R.layout.place_list_item, 10) }

    private val places: ArrayList<Place> by lazy {arrayListOf<Place>()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places_list)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProvider(this, viewModelFactory).get(PlacesViewModel::class.java)

        placesRecyclerView.adapter = placesAdapter

        skeleton.showSkeleton()
        snapHelper.attachToRecyclerView(placesRecyclerView)

        bindUI()

    }

    private fun bindUI() = GlobalScope.launch(Dispatchers.Main) {

        viewModel.getAllPlaces().observe(this@PlacesListActivity, Observer { location ->
            if(location == null || location.isEmpty())
                return@Observer

            updatePlacesList(location)

        })

        viewModel.getDownloadingStatus().observe(this@PlacesListActivity, Observer { status ->
            if(!status)
                Toast.makeText(this@PlacesListActivity, "No internet connection. Please check your device connection settings.", Toast.LENGTH_LONG).show()
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