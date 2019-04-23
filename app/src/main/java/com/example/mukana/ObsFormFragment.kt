package com.example.mukana

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_obsform.*

class ObsFormFragment : BaseMvRxFragment(), AdapterView.OnItemSelectedListener {

    private val geoLocator: GeoLocator = GeoLocator()
    private var lastKnownLocation: String = "lat.: N/A, lng.: N/A"

    // view models keep track of app state even if the fragment is destroyed
    private val itemViewModel: ObsItemViewModel by fragmentViewModel(ObsItemViewModel::class)
    private val listViewModel: ObsListViewModel by activityViewModel(ObsListViewModel::class)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_obsform, container, false)
    } // onCreateView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        hideFloatingActionButton()
        initRaritySpinner()
        setInitialFieldValues() // note: must be called after initRaritySpinner
        setListeners()
    } // onActivityCreated

    override fun onPause() {
        super.onPause()
        geoLocator.stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        geoLocator.startLocationUpdates()
    }

    private fun onCancelButtonClick() {

        // on cancel, clear the old values.
        // I imagine this would be the preferred behaviour of most users.
        itemViewModel.resetState()
    }

    private fun onCreateButtonClick() {

        withState(itemViewModel) {stateBirdObs -> {

            listViewModel.addListItem(stateBirdObs) // auto-updates the listView
        }}
    }

    // i.e., a spinner item
    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {

        val rarityAsString = parent.getItemAtPosition(pos).toString()
        // to deal with the lack of underscore in the user-visible text. not ideal, but i's the simplest way i can think of.
        val rarity = if (rarityAsString == "EXTREMELY RARE") Rarity.EXTREMELY_RARE else Rarity.valueOf(rarityAsString)

        itemViewModel.updateState(ObsItemViewModel.Updating.RARITY, rarity)
    } // onItemSelected

    override fun onNothingSelected(parent: AdapterView<*>) {
        // no need to do anything
    }

    private fun hideFloatingActionButton() {

        // it's part of the activity (not sure if this is kosher, but it seems to work)
        activity!!.floatingActionButton.hide()
    }

    private fun initRaritySpinner() {

        ArrayAdapter.createFromResource(
            context!!, // there is always a context at this point; not sure why it needs this
            R.array.rarity_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            raritySpinner.adapter = adapter
            raritySpinner.onItemSelectedListener = this
        }
    } // initRaritySpinner

    // restore the state of the form fields from the viewmodel (assuming the fields have been dirtied)
    private fun setInitialFieldValues() {

        withState(itemViewModel) { stateBirdObs -> {

            if (speciesET.hint.isBlank()) {

                speciesET.text = stateBirdObs.species.toEditable()
            }

            raritySpinner.setSelection(stateBirdObs.rarity.ordinal)

            if (notesET.hint.isBlank()) {

                notesET.text = stateBirdObs.notes.toEditable()
            }
        }} // withState
    } // setInitialFieldValues

    // called automatically on viewmodel state updates by MvRx
    override fun invalidate() {

        // logically, it should NOT be called in this fragment, as the form should persist until
        // it's manually exited. not sure how to prevent it... will have to see how this works.
    }

    // there's a fancy new way of setting these (data binding), but i've no time to use *all* new features.
    private fun setListeners() {

        cancelButton.setOnClickListener {

            onCancelButtonClick()
        }

        createButton.setOnClickListener {

            onCreateButtonClick()
        }

        val speciesValidator = object : TextValidator(speciesET) {

            override fun validate(textView: TextView, text: String) {

            }
        }

        val notesValidator = object : TextValidator(notesET) {

            override fun validate(textView: TextView, text: String) {

            }
        }

        speciesET.addTextChangedListener(speciesValidator)
        notesET.addTextChangedListener(notesValidator)

        // there should be a clear method as well, but I'd think they're destroyed with the fragment (after some time, anyway)
    } // setListeners

    // we only need geolocation in this class, so I've included it as an inner class
    // for convenience (due to context issues).
    inner class GeoLocator {

        private val locationRequest= LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        // the complaint about the uncertain context is puzzling; I don't think the Activity can
        // disappear before the Fragment that it contains, so this should be safe
        private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context!!)

        private val locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {

                locationResult ?: return

                // there should only ever be one result
                val loc = locationResult.locations[0]
                val lat = loc.latitude.toString().substring(0, 5)
                val lng = loc.longitude.toString().substring(0, 5)

                lastKnownLocation = "lat.: $lat, lng.: $lng"
                log(lastKnownLocation)
            } // onLocationResult
        } // locationCallback

        fun startLocationUpdates() {

            // TODO: add the SettingsRequest stuffs here

            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    null /* Looper */)
            } catch (e: SecurityException) {

                log("GeoLocException: $e")
            }
        } // startLocationUpdates

        fun stopLocationUpdates() {

            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    } // GeoLocator

} // ObsFormFragment