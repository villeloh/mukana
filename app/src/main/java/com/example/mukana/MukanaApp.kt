package com.example.mukana

import android.app.Application
import android.location.Location
import android.text.Editable
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.airbnb.mvrx.BaseMvRxActivity

class MukanaApp : Application() {

    // did some setup here for a library, but it's deleted now.
    // perhaps move the extensions elsewhere?
}

// EXTENSION FUNCTIONS

// simplify the API for manipulating fragments
private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().addToBackStack(null).commit()
}

fun BaseMvRxActivity.addFragment(fragment: Fragment, containerId: Int){
    supportFragmentManager.inTransaction { add(containerId, fragment) }
}

fun BaseMvRxActivity.replaceFragment(fragment: Fragment, containerId: Int) {
    supportFragmentManager.inTransaction { replace(containerId, fragment) }
}

// to silence kotlin's complaint about assigning a String to an Editable
fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

// helper function for obtaining shortened notes strings (to show in the ui).
// I'm sure something similar exists already, but it was easier to make this than to google it.
fun String.truncate(newLength: Int) : String {

    if (newLength <= this.length) return this

    return this.substring(0, newLength)
}

// helper for ui representation
fun Location.formattedCoordString(): String {

    return "lat.: ${this.latitude.toString().substring(0,5)}, lng.: ${this.longitude.toString().substring(0,5)}"
}

// for logging when developing. not sure why it complains about unused parameter
fun Any.log(msg: String) {

    Log.d("JUUH", msg)
}