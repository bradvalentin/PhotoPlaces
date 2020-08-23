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
import kotlinx.android.synthetic.main.activity_place_details.*


class PlaceDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlaceDetailsBinding
    private lateinit var sharedViewModel: SharedViewModel
    val place: Place by lazy { intent.getParcelableExtra<Place>("place") }
    var placeChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_place_details)

        binding.place = place

        sharedViewModel = ViewModelProviders.of(this).get(SharedViewModel::class.java)

        setupToolbar()

        editButton.setOnClickListener {
            showDialog()
            editButton.visibility = INVISIBLE
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
        editButton.visibility = VISIBLE

        if(placeChanged) {
            val returnIntent = Intent()
            returnIntent.putExtra("newPlace", binding.place)
            setResult(Activity.RESULT_OK, returnIntent)
        }
        super.onBackPressed()

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showDialog() {

        val bundle = Bundle()
        bundle.putParcelable("place", binding.place)
        val currentFragment = NewPlaceFragment()
        currentFragment.arguments = bundle

        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, 0, 0, android.R.anim.fade_out)
            .replace(R.id.fragmentHolder, currentFragment)
            .addToBackStack("newPlaceFragment")
            .commit()
    }

}