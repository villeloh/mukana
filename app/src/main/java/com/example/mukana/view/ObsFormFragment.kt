package com.example.mukana.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.airbnb.mvrx.*
import com.example.mukana.*
import com.example.mukana.R
import com.example.mukana.model.Rarity
import com.example.mukana.viewmodel.ObsItemViewModel
import com.example.mukana.viewmodel.ObsListViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_obsform.*

private const val MIN_LENGTH_SPECIES = 1
private const val MAX_LENGTH_SPECIES = 20
private const val MAX_LENGTH_NOTES = 100

class ObsFormFragment : BaseMvRxFragment(), AdapterView.OnItemSelectedListener {

    private lateinit var geoLocator: GeoLocator

    // view models keep track of app state even if the fragment is destroyed
    private val itemViewModel: ObsItemViewModel by fragmentViewModel(ObsItemViewModel::class)
    private val listViewModel: ObsListViewModel by existingViewModel(ObsListViewModel::class) // i.e., the one created by the list view

    private var speciesValid = false
    private var notesValid = false

    private val formValid: Boolean
        get() = speciesValid && notesValid // other values are always valid

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_obsform, container, false)
    } // onCreateView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        geoLocator = GeoLocator(context!!) // the context should exist now

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

    override fun onDestroy() {
        super.onDestroy()
        geoLocator.stopLocationUpdates() // for certainty
    }

    private fun onCancelButtonClick() {

        // on cancel, clear the old values.
        // I imagine this would be the preferred behaviour of most users.
        itemViewModel.resetState()
        showFloatingActionButton()

        returnToListView()
    }

    private fun onCreateButtonClick() {

        if (!formValid) return
        //TODO: show a warning about invalid fields

        // geoloc gets auto-updated, and species, rarity and notes are updated by other ui actions,
        // so we only need to set the timestamp here
        itemViewModel.setTimeStamp(getCurrentTime())
        // we need to sleep for a while to remember our async-set time value.
        // there are ways to deal properly with this in MvRx, but as they're rather elaborate, I'd rather do this and
        // focus on the requested features.
        Thread.sleep(10)

        // could write a function for it, but this is the only place where it'd get called
        withState(itemViewModel) {

            listViewModel.addItem(it) // auto-updates the listView
        }
        showFloatingActionButton()

        returnToListView()
    } // onCreateButtonClick

    // i.e., a spinner item
    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {

        val rarityAsString = parent.getItemAtPosition(pos).toString()
        // to deal with the lack of underscore in the user-visible text. not ideal, but i's the simplest way i can think of.
        val rarity = if (rarityAsString == "EXTREMELY RARE") Rarity.EXTREMELY_RARE else Rarity.valueOf(rarityAsString)

        itemViewModel.setRarity(rarity)
    } // onItemSelected

    override fun onNothingSelected(parent: AdapterView<*>) {
        // no need to do anything
    }

    // the floating action button was losing its styles when navigating back
    // to the list view, so I wrapped it in a FrameLayout as an ad-hoc solution.
    // although not a good habit to get into, the performance hit is minuscule
    // compared to the saved development time.
    private fun hideFloatingActionButton() {

        // it's part of the activity (not sure if this is kosher, but it seems to work)
        activity!!.fabFrameLayout.visibility = View.GONE
    }

    private fun showFloatingActionButton() {

        activity!!.fabFrameLayout.visibility = View.VISIBLE
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

    // restore the state of the form fields from the view model (assuming the fields have been dirtied)
    private fun setInitialFieldValues() {

        withState(itemViewModel) { stateBirdObs -> {

            if (speciesET.hint.isBlank()) {

                speciesET.text = stateBirdObs.UI.species.toEditable()
            }

            raritySpinner.setSelection(stateBirdObs.rarity.ordinal)

            if (notesET.hint.isBlank()) {

                notesET.text = stateBirdObs.notes.toEditable()
            }
        }} // withState
    } // setInitialFieldValues

    // there is a new, fancy way of doing navigation (NavHost, NavController, etc), but I thought
    // I'd rather focus on the requested features. navigation is very simple to handle with just the
    // floating action button and the cancel button in this small app.
    private fun returnToListView() {

        val listFragment = ObsListFragment()
        replaceFragment(listFragment, R.id.fragment_holder) // move back to the main list view
    }

    // called automatically on view model state updates by MvRx
    override fun invalidate() {

        // logically, it should NOT be called in this fragment, as the form should persist until
        // it's manually exited. not sure how to prevent it... will have to see how this works.
    }

    // there's a fancy new way of setting these (data binding), but i've no time to use *all* the new features.
    private fun setListeners() {

        cancelButton.setOnClickListener {

            onCancelButtonClick()
        }

        createButton.setOnClickListener {

            onCreateButtonClick()
        }

        val speciesValidator = object : TextValidator(speciesET) {

            override fun validate(textView: TextView, text: String) {

                if (text.length < MIN_LENGTH_SPECIES || text.length > MAX_LENGTH_SPECIES) {
                    //TODO: show a warning about text length
                } else {

                    speciesValid = true
                    itemViewModel.setSpecies(text)
                }
            } // validate
        } // speciesValidator

        val notesValidator = object : TextValidator(notesET) {

            override fun validate(textView: TextView, text: String) {

                if (text.length > MAX_LENGTH_NOTES) {
                    //TODO: show a warning about text length
                } else {

                    notesValid = true
                    itemViewModel.setNotes(text)
                }
            }
        } // notesValidator

        speciesET.addTextChangedListener(speciesValidator)
        notesET.addTextChangedListener(notesValidator)

        // there should be a clearListeners method as well, but I think they're destroyed with the fragment (after some time, anyway)
    } // setListeners

    private fun getCurrentTime(): Long {

        return System.currentTimeMillis()
    }

    // we only need geolocation in this class, so I included it as an inner class
    // for convenience.
    inner class GeoLocator(context: Context) {

        private val locationRequest = LocationRequest.create()?.apply {

            // setting too short of a value leads to battery drain; otoh, if the value is too long,
            // a quickly entered observation will have no location value
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        private val locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {

                locationResult ?: return

                // there should only ever be one result
                val loc = locationResult.locations[0]
                itemViewModel.setGeoLoc(loc)
/*
                val lat = loc.latitude.toString().substring(0, 5)
                val lng = loc.longitude.toString().substring(0, 5)

                val lastKnownLocation = "lat.: $lat, lng.: $lng"
                log(lastKnownLocation) */
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