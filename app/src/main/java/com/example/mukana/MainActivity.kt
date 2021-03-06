package com.example.mukana

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import com.airbnb.mvrx.BaseMvRxActivity
import com.example.mukana.model.BirdObservation
import com.example.mukana.view.ObsFormFragment
import com.example.mukana.view.ObsListFragment

import kotlinx.android.synthetic.main.activity_main.*

/**
 * A bird observation list app for CGI.
 * @author Ville Lohkovuori (April 2019)
 * */

class MainActivity : BaseMvRxActivity(), ObsListFragment.OnListFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        if (hasPermissions()) {

            val listFragment = ObsListFragment()
            addFragment(listFragment, R.id.fragment_holder) // extension method
        } else {
            requestPermissions()
        }

        floatingActionButton.setOnClickListener {

            val formFragment = ObsFormFragment()
            replaceFragment(formFragment, R.id.fragment_holder)
        }
    } // onCreate

    private fun hasPermissions(): Boolean {

        for (permission in PERMISSIONS) {

            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {

                return false
            }
        }
        return true
    } // hasPermissions

    private fun requestPermissions() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION) ||
            ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
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

                    val listFragment = ObsListFragment()
                    addFragment(listFragment, R.id.fragment_holder) // extension method
                } else {
                    // permission denied. not sure what to do here...
                }
            }
            else -> {
                // Ignore all other requests.
            }
        } // when
    } // onRequestPermissionsResult

    // called on clicking the list items. could be used for entering
    // a detail / edit view.
    override fun onListFragmentInteraction(item: BirdObservation) { }

    companion object {

        private const val ALL_PERMISSIONS = 1

        @JvmStatic
        private val PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE)
    } // companion object

} // MainActivity