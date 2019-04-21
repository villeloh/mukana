package com.example.mukana

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.airbnb.mvrx.BaseMvRxActivity

class MukanaApp : Application() {

    override fun onCreate() {
        super.onCreate()

    }
}

// EXTENSION FUNCTIONS
// (these fit best in this file)

// simplify the API for manipulating fragments
private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun BaseMvRxActivity.addFragment(fragment: Fragment, containerId: Int){
    supportFragmentManager.inTransaction { add(containerId, fragment) }
}


fun BaseMvRxActivity.replaceFragment(fragment: Fragment, containerId: Int) {
    supportFragmentManager.inTransaction { replace(containerId, fragment) }
}