package com.example.mukana

import android.location.Location
import com.airbnb.mvrx.MvRxState

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
) : MvRxState