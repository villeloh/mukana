package com.example.mukana

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import com.airbnb.mvrx.BaseMvRxActivity
import com.google.android.gms.location.*

import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : BaseMvRxActivity(), ObsListFragment.OnListFragmentInteractionListener {

    private val listFragment: ObsListFragment = ObsListFragment()
    private lateinit var geoLocator: GeoLocator
    private lateinit var lastKnownLocation: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //TODO: adding the fragment erases the permission acceptance dialog... fix asap!
        addFragment(listFragment, R.id.fragment_holder) // extension method from MukanaApp.kt

        geoLocator = GeoLocator()
        geoLocator.startLocationUpdates()

        floatingActionButton.setOnClickListener {

            val formFragment = ObsFormFragment()
            replaceFragment(formFragment, R.id.fragment_holder)
           /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show() */
        }
    } // onCreate

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onListFragmentInteraction(item: BirdObservation) {


    }

/*
    private fun setMainView() {

        val context = ComponentContext(this)

        val button = ButtonContainer.create(context)
            .text("huu")
            .build()

        val lithoRecyclerView = RecyclerCollectionComponent.create(context)
            .disablePTR(true) // pull to refresh
            .section(ObsListSection.create(SectionContext(context)).build())
            .build()

        val components: Component = Column.create(context)
            .child(button)
            .child(lithoRecyclerView)
            .build()

        val lithoView = LithoView.create(context, components)
        setContentView(lithoView)
    } // setMainView */

    companion object {

        @JvmStatic
        private var permissionsGranted = false
        private const val ALL_PERMISSIONS = 1
        @JvmStatic
        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.INTERNET)

        @JvmStatic
        private val LOCATION_REQUEST = LocationRequest.create()?.apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

    } // companion object

    override fun onPause() {
        super.onPause()
        geoLocator.stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        /* if (permissionsGranted) */ geoLocator.startLocationUpdates()
    }

    private fun handlePermissions() {

        if (hasPermissions()) {

            permissionsGranted = true
        } else {

            permissionsGranted = false
            requestPermissions()
        }
    } // handlePermissions

    private fun hasPermissions(): Boolean {
        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    private fun requestPermissions() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.INTERNET) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_NETWORK_STATE)) {
            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.
            // TODO: do this stuff...
        } else {
            // we can request the permission.
            ActivityCompat.requestPermissions(this, PERMISSIONS, ALL_PERMISSIONS)
        }
    } // requestPermissions

    // callback of requestPermissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {

            ALL_PERMISSIONS -> {
                // If the request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    handlePermissions()
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission
                }
            }
            else -> {
                // Ignore all other requests.
            }
        } // when
    } // onRequestPermissionsResult

    // it's only here because it's too much trouble to deal with the context issues...
    //TODO: try to move it into its own class
    inner class GeoLocator {

        private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)

        private val locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult?) {

                locationResult ?: return

                // there should only ever be one result
                val loc = locationResult.locations[0]
                val lat = loc.latitude.toString().substring(0, 5)
                val lng = loc.longitude.toString().substring(0, 5)

                lastKnownLocation = "$lat, $lng"
                Log.d("VITTU", lastKnownLocation)
            } // onLocationResult
        } // locationCallback

        fun startLocationUpdates() {

            // TODO: add the SettingsRequest stuffs here

            try {
                fusedLocationClient.requestLocationUpdates(LOCATION_REQUEST,
                    locationCallback,
                    null /* Looper */)
            } catch (e: SecurityException) {
                // TODO: handle exception...
            }
        } // startLocationUpdates

        fun stopLocationUpdates() {

            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    } // GeoLocator

} // MainActivity

