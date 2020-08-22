package com.example.photoplaces.ui.placeDetails

import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil

import com.example.photoplaces.R
import com.example.photoplaces.data.entity.Place
import com.example.photoplaces.databinding.ActivityPlaceDetailsBinding
import kotlinx.android.synthetic.main.activity_place_details.*


class PlaceDetailsActivity : AppCompatActivity() {

    val place: Place by lazy { intent.getParcelableExtra<Place>("place") }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding: ActivityPlaceDetailsBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_place_details)

        binding.place = place

        setupToolbar()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = place.label
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(0, R.anim.fade_out);
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}