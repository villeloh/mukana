package com.example.mukana.viewmodel

import android.location.Location
import com.example.mukana.log
import com.example.mukana.model.BirdObservation
import com.example.mukana.model.Rarity
import java.sql.Timestamp

class ObsItemViewModel(private val initialState: BirdObservation) : MvRxViewModel<BirdObservation>(initialState) {

    // NOTE: to make it easier to work with the state logic between different files,
    // the getters are located in the BirdObservation class itself (under the 'UI' namespace).

    // I'd rather have used proper getters and setters, but there were a number of issues
    // when trying to implement them. while repetitive, this was the best compromise approach
    // that I found (another would've been to store only Strings in BirdObservation).
    fun setSpecies(value: String) = setState { copy(species = value) }

    fun setRarity(value: Rarity) = setState { copy(rarity = value) }

    fun setNotes(value: String) = setState { copy(notes = value) }

    fun setGeoLoc(value: Location) = setState { copy(geoLocation = value) }

    fun setTimeStamp(value: Long) = setState { copy(timeStamp = value) }

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