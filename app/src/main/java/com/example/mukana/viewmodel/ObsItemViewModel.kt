package com.example.mukana.viewmodel

import android.location.Location
import com.example.mukana.model.BirdObservation
import com.example.mukana.model.Rarity

class ObsItemViewModel(private val initialState: BirdObservation) : MvRxViewModel<BirdObservation>(initialState) {

    enum class Accessing {
        SPECIES,
        RARITY,
        NOTES,
        GEOLOC,
        TIMESTAMP
    }

    // this way, we only need one method.
    fun updateState(type: Accessing, value: Any) {

        when (type) {
            Accessing.SPECIES -> setState { copy(species = value as String) }
            Accessing.RARITY -> setState { copy(rarity = value as Rarity) }
            Accessing.NOTES -> setState { copy(notes = value as String) }
            Accessing.GEOLOC -> setState { copy(geoLocation = value as Location) }
            Accessing.TIMESTAMP -> setState { copy(timeStamp = value as Long) }
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