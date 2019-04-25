package com.example.mukana.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.airbnb.mvrx.MvRxState
import com.example.mukana.formattedUIString
import com.example.mukana.truncate
import java.text.SimpleDateFormat
import java.util.*

enum class Rarity(val text: String) {
    COMMON("common"),
    RARE("rare"),
    EXTREMELY_RARE("extremely rare")
}

// these will be stored in the local Room database & used by view models as well.
@Entity(tableName = "bird_obs")
data class BirdObservation(
    @ColumnInfo(name = "species") val species: String = "",
    @ColumnInfo(name = "rarity") val rarity: Rarity = Rarity.COMMON,
    @ColumnInfo(name = "notes") val notes: String = "",
    @Embedded val geoLocation: Coords = Coords(0.1234567,0.1234567),
    @PrimaryKey val timeStamp: Long = 0L
) : MvRxState

fun valueToUIString(value: Any, type: Accessing): String {

    return when(type) {
        Accessing.SPECIES -> value as String
        Accessing.RARITY -> (value as Rarity).text
        Accessing.GEOLOC -> (value as Coords).formattedUIString()
        Accessing.NOTES -> (value as String).truncate(20)
        Accessing.TIMESTAMP -> longToFormattedDateString(value as Long)
    }
}

enum class Accessing {
    SPECIES,
    RARITY,
    NOTES,
    GEOLOC,
    TIMESTAMP
}

// for storing the location coordinates in the Room database
// (Room really seems to hate complex objects).
data class Coords(
    @ColumnInfo(name = "lat") val lat: Double,
    @ColumnInfo(name = "lng") val lng: Double
)

private fun longToFormattedDateString(timeStamp: Long): String {

    val formatter = SimpleDateFormat("dd.MM. yyyy", Locale.ENGLISH)
    return formatter.format(timeStamp)
}