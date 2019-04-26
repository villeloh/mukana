package com.example.mukana

import android.app.Activity
import android.graphics.Point
import android.text.Editable
import android.util.Log
import android.view.Gravity
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.airbnb.mvrx.BaseMvRxActivity
import com.airbnb.mvrx.BaseMvRxFragment

/*
* For helpful extension functions (in order not to bloat their associated classes too much).
**/

// to simplify the API for manipulating fragments
private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().addToBackStack(null).commit()
}

fun BaseMvRxActivity.addFragment(fragment: Fragment, containerId: Int){
    supportFragmentManager.inTransaction { add(containerId, fragment) }
}

fun BaseMvRxActivity.replaceFragment(fragment: Fragment, containerId: Int) {
    supportFragmentManager.inTransaction { replace(containerId, fragment) }
}

fun BaseMvRxFragment.replaceFragment(fragment: Fragment, containerId: Int) {
    activity!!.supportFragmentManager.inTransaction { replace(containerId, fragment) }
}

// to silence kotlin's complaint about assigning a String to an Editable
fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

// helper function for obtaining shortened strings (to show in the ui).
// I'm sure something similar exists already, but it was easier to make this than to google it.
fun String.truncate(newLength: Int) : String {

    // this seems to cause an infinite loop under some circumstances (erasing text from an EditText).
    // I worked around it, but I'm curious as to what might be causing it.
    if (newLength >= this.length) return this

    return this.substring(0, newLength)
} // String.truncate

// for logging when developing. (not sure why it complains about unused parameter)
fun Any.log(msg: String) {

    Log.d("JUUH", msg)
}

// an easy Toast is a happy Toast :)
fun Fragment.toast(msg: String) {

    val act = activity ?: return
    val screenHeight = act.getScreenHeight()
    val t = Toast.makeText(context, msg, Toast.LENGTH_SHORT)
    t.setGravity(Gravity.TOP, 0, (screenHeight*0.20).toInt()) // 20 % from the top of the screen
    t.show()
} // Fragment.toast

private fun Activity.getScreenHeight(): Int {

    val display = windowManager.defaultDisplay
    val size = Point()
    display.getSize(size)
    return size.y
} // Activity.getScreenHeight