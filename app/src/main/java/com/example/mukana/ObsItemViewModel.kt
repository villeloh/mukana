package com.example.mukana

import android.location.Location

class ObsItemViewModel(private val initialState: BirdObservation) : MvRxViewModel<BirdObservation>(initialState) {

    init {

    }

    enum class Updating {
        SPECIES,
        RARITY,
        NOTES,
        GEOLOC,
        TIMESTAMP
    }

    // this way, we only need one method.
    fun updateState(type: Updating, value: Any) {

        when (type) {
            Updating.SPECIES -> setState { copy(species = value as String) }
            Updating.RARITY -> setState { copy(rarity = value as Rarity) }
            Updating.NOTES -> setState { copy(notes = value as String) }
            Updating.GEOLOC -> setState { copy(geoLocation = value as Location) }
            Updating.TIMESTAMP -> setState { copy(timeStamp = value as Long) }
        }
    } // updateState

    fun resetState() {

        // i'm sure there's a better way to do this, but it works
        setState { copy(
            species = initialState.species,
            rarity = initialState.rarity,
            notes = initialState.notes,
            geoLocation = initialState.geoLocation,
            timeStamp = initialState.timeStamp)
        }
    } // resetState

} // ObsItemViewModel