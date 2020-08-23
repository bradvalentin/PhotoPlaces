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
import com.example.photoplaces.R
import com.example.photoplaces.databinding.FragmentPlaceDialogBinding
import com.example.photoplaces.utils.hideKeyboard
import org.koin.android.ext.android.inject

class NewPlaceFragment : Fragment() {

    private val viewModelFactory: NewPlaceFragmentViewModelFactory by inject()
    private lateinit var viewModel: NewPlaceFragmentViewModel
    lateinit var binding: FragmentPlaceDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_place_dialog, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel =
            ViewModelProvider(this, viewModelFactory).get(NewPlaceFragmentViewModel::class.java)
        binding.viewmodel = viewModel

        viewModel.formMediator.observe(this, Observer { validationResult ->
            binding.saveButton.isEnabled = validationResult
        })

        viewModel.latitudeFieldMediator.observe(this, Observer { validationResult ->
            if (validationResult)
                binding.textInputLayoutLatitude.error = null
            else
                binding.textInputLayoutLatitude.error = getString(R.string.latitude_range_text)
        })

        viewModel.longitudeFieldMediator.observe(this, Observer { validationResult ->
            if (validationResult)
                binding.textInputLayoutLongitude.error = null
            else
                binding.textInputLayoutLongitude.error = getString(R.string.longitude_range_text)
        })

        viewModel.placeUpdatedOrInsertedLiveData.observe(this, Observer {
            it?.let {
                activity?.onBackPressed()
                activity?.hideKeyboard()
            } ?: run {
                Toast.makeText(activity, getString(R.string.fail_to_save_text), Toast.LENGTH_LONG)
                    .show()
            }
        })
    }


}

