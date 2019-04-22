package com.example.mukana

import android.app.Application
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

// for logging when developing. not sure why it complains about unused parameter
fun Any.log(msg: String) {

    Log.d("JUUH", msg)
}