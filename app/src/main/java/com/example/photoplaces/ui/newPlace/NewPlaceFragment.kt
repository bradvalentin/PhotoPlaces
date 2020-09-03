package com.example.photoplaces.ui.newPlace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.photoplaces.R
import com.example.photoplaces.data.entity.Place
import com.example.photoplaces.databinding.FragmentPlaceDialogBinding
import com.example.photoplaces.utils.Constants.PARCELABLE_PLACE_KEY
import com.example.photoplaces.utils.hideKeyboard
import org.koin.android.ext.android.inject

class NewPlaceFragment : Fragment() {

    private val viewModelFactory: NewPlaceFragmentViewModelFactory by inject()
    private val newPlaceViewModel: NewPlaceFragmentViewModel by lazy { ViewModelProvider(this, viewModelFactory).get(NewPlaceFragmentViewModel::class.java) }
    private val sharedViewModel: SharedViewModel by lazy { ViewModelProviders.of(requireActivity()).get(SharedViewModel::class.java) }

    private lateinit var binding: FragmentPlaceDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_place_dialog, container, false
        )
        binding.lifecycleOwner = this

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getParcelable<Place>(PARCELABLE_PLACE_KEY)?.let {
            newPlaceViewModel.place = it
        }

        binding.apply {
            viewmodel = newPlaceViewModel
        }
        newPlaceViewModel.bindPlaceData()

        bindUIFormVerification()

        newPlaceViewModel.placeUpdatedOrInsertedLiveData.observe(this, Observer {
            it?.let {
                activity?.apply {
                    onBackPressed()
                    hideKeyboard()
                }
                sharedViewModel.setNewPlace(it)

            } ?: run {
                Toast.makeText(activity, getString(R.string.failed_to_save_text), Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    private fun bindUIFormVerification() {
        newPlaceViewModel.formMediator.observe(this, Observer { validationResult ->
            binding.saveButton.isEnabled = validationResult
        })

     //   newPlaceViewModel.latitudeFieldMediator.observe(this, Observer { validationResult ->
         //   if (validationResult)
               // binding.textInputLayoutLatitude.error = null
         //   else
              //  binding.textInputLayoutLatitude.error = getString(R.string.latitude_range_text)
    //    })
//
//        newPlaceViewModel.longitudeFieldMediator.observe(this, Observer { validationResult ->
//            if (validationResult)
//                binding.textInputLayoutLongitude.error = null
//            else
//                binding.textInputLayoutLongitude.error = getString(R.string.longitude_range_text)
//        })
    }

}

