package com.example.mukana.model

import android.location.Location
import android.util.Log
import com.airbnb.mvrx.MvRxState
import com.example.mukana.formattedCoordString
import com.example.mukana.truncate
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

enum class Rarity(val text: String) {
    COMMON("common"),
    RARE("rare"),
    EXTREMELY_RARE("extremely rare")
}

data class BirdObservation(
    val species: String = "",
    val rarity: Rarity = Rarity.COMMON,
    val notes: String = "",
    val geoLocation: Location = Location(""),
    val timeStamp: Long = 0L
) : MvRxState {

    // a namespace for getting UI-formatted values of the internal fields.
    // NOTE: technically, the view model should be responsible for this logic.
    // however, since the BirdObservations are used for rendering the main list view
    // as well as for keeping form state, it makes sense to have the formatting in the BO class itself.
    val UI = object : UINamespace {

        val bo = this@BirdObservation

        override val species: String
            get() = bo.species

        override val rarity: String
            get() = "( ${bo.rarity.text} )"

        override val notes: String
            get() = bo.notes.truncate(20) // custom helper method

        override val geoLoc: String
            get() = bo.geoLocation.formattedCoordString() // helper method

        override val timeStamp: String
            get() = longToFormattedDateString(bo.timeStamp)
    } // UI

} // BirdObservation

// in order to get a publicly accessible object namespace, we need this redundant interface.
// first time I've ever been disappointed in Kotlin.
interface UINamespace {

    val species: String
    val rarity: String
    val notes: String
    val geoLoc: String
    val timeStamp: String
}

private fun longToFormattedDateString(timeStamp: Long): String {

    val formatter = SimpleDateFormat("dd.MM. yyyy", Locale.ENGLISH)
    return formatter.format(timeStamp)
}