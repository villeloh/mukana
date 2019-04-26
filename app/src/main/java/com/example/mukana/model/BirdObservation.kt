package com.example.mukana.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.airbnb.mvrx.MvRxState
import com.example.mukana.newLinesToSpaces
import com.example.mukana.truncate
import java.text.SimpleDateFormat
import java.util.*

enum class Rarity(val text: String) {
    COMMON("common"),
    RARE("rare"),
    EXTREMELY_RARE("ext. rare")
}

// these will be stored in the local Room database & used by view models as well.
@Entity(tableName = "bird_obs")
data class BirdObservation(
    @ColumnInfo(name = "species") val species: String = "",
    @ColumnInfo(name = "rarity") val rarity: Rarity = Rarity.COMMON,
    @ColumnInfo(name = "notes") val notes: String = "",
    @Embedded val geoLocation: Coords = Coords(0.0,0.0), // NOTE: do not change these values, as it breaks the UI string formatting!
    @PrimaryKey val timeStamp: Long = 0L
) : MvRxState

// not part of the class itself to avoid issues with the Room db.
fun valueToUIString(value: Any, type: Accessing): String {

    return when(type) {
        Accessing.SPECIES -> value as String // redundant, but it's good to be consistent in case there's future changes
        Accessing.RARITY -> "( ${(value as Rarity).text} )"
        Accessing.GEOLOC -> (value as Coords).formattedUIString()
        Accessing.NOTES -> newLinesToSpaces((value as String).truncate(20)) // when shown in the list view, newlines are only a nuisance
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

// helper for ui representation.
private fun Coords.formattedUIString(): String {

    val latString = this.lat.toString()
    val lngString = this.lng.toString()

    // i.e., it is the default, 'empty' Coords object.
    // a crude check, but it works for everyone except for the rare, brave sailor somewhere near West Africa.
    if (latString == "0.0" && lngString == "0.0") return "Location: N/A"

    val latDecims = latString.length
    val lngDecims = lngString.length

    return "lat.: ${latString.substring(0, latDecims-4)}, lng.: ${lngString.substring(0, lngDecims-4)}"
} // Coords.formattedUIString

private fun longToFormattedDateString(timeStamp: Long): String {

    val formatter = SimpleDateFormat("dd.MM. yyyy", Locale.ENGLISH)
    return formatter.format(timeStamp)
}