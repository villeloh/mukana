package com.example.mukana.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.airbnb.mvrx.MvRxState
import com.example.mukana.truncate
import java.text.SimpleDateFormat
import java.util.*

private const val NOTES_BLURB_LENGTH = 40
private const val NO_NOTES_UI_STR = "( no notes )"
private const val NO_LOCATION_STR = "Location: N/A"
private val EMPTY_COORDS = Coords(0.0, 0.0)

// these will be stored in the local Room database & used by view models as well.
@Entity(tableName = "bird_obs")
data class BirdObservation(
    @ColumnInfo(name = "species") val species: String = "",
    @ColumnInfo(name = "rarity") val rarity: Rarity = Rarity.COMMON,
    @ColumnInfo(name = "notes") val notes: String = "",
    @ColumnInfo(name = "image_path") val imagePath: String = "", // NOTE: the images could be moved, which would invalidate the data.
    // but at the same time, saving the whole image in the db is inadvisable. not sure what would be optimal, but this was by far the easiest solution.
    @Embedded val geoLocation: Coords = EMPTY_COORDS, // NOTE: do not change this assignment, as it breaks the UI string formatting!
    @PrimaryKey val timeStamp: Long = 0L
) : MvRxState

// not part of the class itself to avoid any possible issues with the Room db.
fun valueToUIString(value: Any, type: Accessing): String {

    return when(type) {
        Accessing.SPECIES -> value as String // redundant, but it's good to be consistent in case there's future changes
        Accessing.RARITY -> "* ${(value as Rarity).text} *"
        Accessing.GEOLOC -> (value as Coords).formattedUIString()
        Accessing.NOTES -> {

            val str = value as String
            val isOverLimit = str.length > NOTES_BLURB_LENGTH
            val str2 = when {
                isOverLimit -> "${str.truncate(NOTES_BLURB_LENGTH)}..."
                str == "" -> NO_NOTES_UI_STR // easier than making the view disappear (and the ui looks neater with all views present, imo)
                else -> str
            }

            newLinesToSpaces(str2) // when shown in the list view, newlines are only a nuisance
        }
        Accessing.TIMESTAMP -> longToFormattedDateString(value as Long)
        Accessing.IMAGE_PATH -> value as String
    } // when
} // valueToUIString

enum class Rarity(val text: String) {
    COMMON("common"),
    RARE("rare"),
    EXTREMELY_RARE("ext. rare")
}

enum class Accessing {
    SPECIES,
    RARITY,
    NOTES,
    GEOLOC,
    TIMESTAMP,
    IMAGE_PATH
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
    if (this == EMPTY_COORDS) return NO_LOCATION_STR

    val latDecims = latString.length
    val lngDecims = lngString.length

    return "lat.: ${latString.substring(0, latDecims-4)}, lng.: ${lngString.substring(0, lngDecims-4)}"
} // Coords.formattedUIString

private fun longToFormattedDateString(timeStamp: Long): String {

    val formatter = SimpleDateFormat("dd.MM. yyyy", Locale.ENGLISH)
    return formatter.format(timeStamp)
}

// for removing newlines from Strings (for ui display purposes).
// (I didn't extend String with it because I want to avoid any 'this' issues.)
private fun newLinesToSpaces(str: String): String {

    val separator = System.getProperty("line.separator") ?: return str
    return str.replace(separator, " ") // replace with a space so that we don't glue the words together
}