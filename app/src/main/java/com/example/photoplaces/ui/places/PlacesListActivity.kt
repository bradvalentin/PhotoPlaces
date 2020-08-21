package com.example.photoplaces.ui.places

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.photoplaces.R
import com.example.photoplaces.data.entity.Place
import com.example.photoplaces.utils.CarouselSnapHelper
import com.faltenreich.skeletonlayout.Skeleton
import com.faltenreich.skeletonlayout.applySkeleton
import kotlinx.android.synthetic.main.activity_places_list.*

class PlacesListActivity : AppCompatActivity(), PlaceItemClickListener {

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

        placesRecyclerView.adapter = placesAdapter

        skeleton.showSkeleton()
        snapHelper.attachToRecyclerView(placesRecyclerView)

    }

    override fun placeItemPressed(place: Place) {
        Toast.makeText(this, place.address, Toast.LENGTH_SHORT).show()
    }

}