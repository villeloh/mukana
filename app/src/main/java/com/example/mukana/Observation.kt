package com.example.mukana

import android.location.Location
import java.sql.Timestamp

enum class Rarity(val text: String) {
    COMMON("common"),
    RARE("rare"),
    EXTREMELY_RARE("extremely rare")
}

data class Observation(
    val species: String,
    val rarity: Rarity,
    val notes: String,
    val geoLocation: Location,
    val timeStamp: Timestamp
)