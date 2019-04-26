package com.example.mukana.view

import android.app.Activity.RESULT_OK
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
import com.example.mukana.model.Coords
import com.example.mukana.model.Rarity
import com.example.mukana.viewmodel.ObsItemViewModel
import com.example.mukana.viewmodel.ObsListViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_obsform.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.provider.MediaStore


/*
* The form for entering new observations.
*/

private const val MIN_LENGTH_SPECIES = 1
private const val MAX_LENGTH_SPECIES = 20
private const val MAX_LENGTH_NOTES = 100

private const val FORM_INVALID_GENERAL = "Form invalid!"
private const val FORM_INVALID_SPECIES = " Please enter a species ($MIN_LENGTH_SPECIES - $MAX_LENGTH_SPECIES characters)."
private const val FORM_INVALID_NOTES = " Max length exceeded! Please enter at max $MAX_LENGTH_NOTES characters."

private const val LOAD_IMAGE = 1

class ObsFormFragment : BaseMvRxFragment(), AdapterView.OnItemSelectedListener {

    private lateinit var geoLocator: GeoLocator

    // view models keep track of app state even if the fragment is destroyed
    private val itemViewModel: ObsItemViewModel by fragmentViewModel(ObsItemViewModel::class)
    private val listViewModel: ObsListViewModel by existingViewModel(ObsListViewModel::class) // i.e., the one created by the list view

    private var speciesValid = false
    private var notesValid = true // since they're optional

    private val formValid: Boolean
        get() = speciesValid && notesValid // other values are always valid

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_obsform, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        geoLocator = GeoLocator(context!!) // the context should exist now

        hideFloatingActionButton()
        initRaritySpinner(context!!)
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
        // (I imagine this is the preferred behaviour of most users.)
        itemViewModel.resetState()
        showFloatingActionButton()
        returnToListView()
    } // onCancelButtonClick

    private fun onCreateButtonClick() {

        if (!formValid) {

            val msgStart = FORM_INVALID_GENERAL
            val speciesPart = if (!speciesValid) FORM_INVALID_SPECIES else " "
            val notesPart = if (!notesValid) FORM_INVALID_NOTES else " "

            toast("$msgStart$speciesPart$notesPart")
            return
        } // if

        // geoloc gets auto-updated, and species, rarity and notes are updated by other ui actions,
        // so we only need to set the timestamp here
        itemViewModel.setTimeStamp(getCurrentTime())

        // we need to sleep for a while to remember our async-set time value.
        // there are ways to deal properly with this in MvRx, but as they're rather elaborate, I'd rather do this and
        // focus on the requested features.
        Thread.sleep(10)

        // could write a function for this, but this is the only place where it'd get called
        withState(itemViewModel) {

            listViewModel.addItem(it) // auto-updates the listView, and adds the item to the database as well
        }

        showFloatingActionButton()
        returnToListView()
    } // onCreateButtonClick

    // i.e., a spinner item
    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {

        val rarityAsString = parent.getItemAtPosition(pos).toString()
        // to deal with the lack of underscore in the user-visible text. not ideal, but it's the simplest way i can think of.
        val rarity = if (rarityAsString == "EXTREMELY RARE") Rarity.EXTREMELY_RARE else Rarity.valueOf(rarityAsString)

        itemViewModel.setRarity(rarity)
    } // onItemSelected

    override fun onNothingSelected(parent: AdapterView<*>) {
        // no need to do anything
    }

    // there is a new, fancy way of doing navigation (NavHost, NavController, etc), but I thought
    // I'd rather focus on the requested features. navigation is very simple in this small app.
    private fun returnToListView() {

        val listFragment = ObsListFragment()
        replaceFragment(listFragment, R.id.fragment_holder)
    }

    private fun onFormImageViewClick() {

        val i = Intent(
            Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(i, LOAD_IMAGE)
    } // onFormImageViewClick

    // returning from the image picking activity
    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode != LOAD_IMAGE || resultCode != RESULT_OK || intent == null || activity == null) return

        val selectedImage = intent.data

        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

        val cursor = activity!!.contentResolver.query(selectedImage!!,
            filePathColumn, null, null, null)

        cursor!!.moveToFirst()

        val columnIndex = cursor.getColumnIndex(filePathColumn[0])
        val picturePath = cursor.getString(columnIndex)
        cursor.close()

        itemViewModel.setImagePath(picturePath)
        formImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath))
    } // onActivityResult

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

    private fun initRaritySpinner(context: Context) {

        ArrayAdapter.createFromResource(
            context,
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

            if (speciesET.isDirty) {

                speciesET.text = stateBirdObs.species.toEditable()
            }

            raritySpinner.setSelection(stateBirdObs.rarity.ordinal)

            if (notesET.isDirty) {

                notesET.text = stateBirdObs.notes.toEditable()
            }
        }} // withState
    } // setInitialFieldValues

    // there's a fancy new way of setting these (data binding), but i've no time to use *all* the new features.
    private fun setListeners() {

        cancelButton.setOnClickListener {

            onCancelButtonClick()
        }

        createButton.setOnClickListener {

            onCreateButtonClick()
        }

        formImageView.setOnClickListener {

            onFormImageViewClick()
        }

        val speciesValidator = object : TextValidator(speciesET) {

            override fun validate(textView: TextView, text: String) {

                val length = text.length
                when {

                    length > MAX_LENGTH_SPECIES -> {

                        speciesValid = false
                        toast("Please enter max $MAX_LENGTH_SPECIES characters.")
                        textView.text = text.truncate(MAX_LENGTH_SPECIES) // it's a little crude, but better than fiddling with isEnabled
                    }
                    length < MIN_LENGTH_SPECIES -> {

                        speciesValid = false
                        toast("Please enter at least $MIN_LENGTH_SPECIES character(s)")
                    }
                    else -> {
                        speciesValid = true
                        itemViewModel.setSpecies(text)
                    }
                } // when
            } // validate
        } // speciesValidator

        val notesValidator = object : TextValidator(notesET) {

            override fun validate(textView: TextView, text: String) {

                if (text.length > MAX_LENGTH_NOTES) {

                    notesValid = false
                    toast("Please do not exceed $MAX_LENGTH_NOTES characters.")
                    textView.text = text.truncate(MAX_LENGTH_NOTES)
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

    // called automatically on view model state updates by MvRx
    override fun invalidate() {

        // logically, it should NOT be called in this fragment, as the form should persist until
        // it's manually exited. it doesn't seem to be causing any issues though.
    }

    // we only need geolocation in this class, so I included it as an inner class for convenience.
    inner class GeoLocator(context: Context) {

        private val locationRequest = LocationRequest.create()?.apply {

            // setting too short of a value leads to battery drain; otoh, if the value is too long,
            // a quickly entered observation will have no location value
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        } // locationRequest

        private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        private val locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {

                locationResult ?: return

                // there should only ever be one result
                val loc = locationResult.locations[0]
                val coords = Coords(loc.latitude, loc.longitude)
                itemViewModel.setGeoLoc(coords)
            } // onLocationResult
        } // locationCallback

        fun startLocationUpdates() {

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