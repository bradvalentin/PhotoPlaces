package com.example.photoplaces.ui.placeDetails

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.photoplaces.R
import com.example.photoplaces.data.entity.Place
import com.example.photoplaces.databinding.ActivityPlaceDetailsBinding
import com.example.photoplaces.ui.newPlace.NewPlaceFragment
import com.example.photoplaces.ui.newPlace.SharedViewModel
import com.example.photoplaces.utils.Constants.NEW_PLACE_FRAGMENT_TAG
import com.example.photoplaces.utils.Constants.PARCELABLE_CHANGED_PLACE_KEY
import com.example.photoplaces.utils.Constants.PARCELABLE_PLACE_KEY
import com.example.photoplaces.utils.setOnSingleClickListener
import kotlinx.android.synthetic.main.activity_place_details.*


class PlaceDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaceDetailsBinding
    private val sharedViewModel: SharedViewModel by lazy { ViewModelProviders.of(this).get(SharedViewModel::class.java) }
    val place: Place by lazy { intent.getParcelableExtra<Place>(PARCELABLE_PLACE_KEY) }
    var placeChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_place_details)

        binding.place = place

        setupToolbar()

        binding.editButton.setOnSingleClickListener {
            showDialog()
            binding.editButton.visibility = INVISIBLE
        }

        sharedViewModel.newPlaceMutableLiveData.observe(this, Observer {
            binding.place = it
            placeChanged = true
        })

    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onBackPressed() {
        overridePendingTransition(0, R.anim.fade_out);
        binding.editButton.visibility = VISIBLE

        if(placeChanged) {
            val returnIntent = Intent().apply {
                putExtra(PARCELABLE_CHANGED_PLACE_KEY, binding.place)
            }
            setResult(Activity.RESULT_OK, returnIntent)
        }
        super.onBackPressed()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showDialog() {

        val bundle = Bundle().apply {
            putParcelable(PARCELABLE_PLACE_KEY, binding.place)
        }
        val currentFragment = NewPlaceFragment().apply {
            arguments = bundle
        }

        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, 0, 0, android.R.anim.fade_out)
            .replace(R.id.fragmentHolder, currentFragment)
            .addToBackStack(NEW_PLACE_FRAGMENT_TAG)
            .commit()
    }

}