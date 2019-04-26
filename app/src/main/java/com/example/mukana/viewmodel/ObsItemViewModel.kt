package com.example.mukana.viewmodel

import com.example.mukana.model.BirdObservation
import com.example.mukana.model.Coords
import com.example.mukana.model.Rarity

/*
* For keeping track of the form state when the form view is open.
* */

class ObsItemViewModel(private val initialState: BirdObservation) : MvRxViewModel<BirdObservation>(initialState) {

    // NOTE: to make it easier to work with the state logic between different files,
    // the getting is done via a method in the BirdObservation class itself.

    // I'd rather have used proper getters and setters, but there were a number of issues
    // when trying to implement them. while repetitive, this was the best compromise approach
    // that I found (another would've been to store only Strings in BirdObservation, or perhaps
    // use generics somehow (I'm still new to them so I judged it too elaborate for now)).
    fun setSpecies(value: String) = setState { copy(species = value) }

    fun setRarity(value: Rarity) = setState { copy(rarity = value) }

    fun setNotes(value: String) = setState { copy(notes = value) }

    fun setGeoLoc(value: Coords) = setState { copy(geoLocation = value) }

    fun setTimeStamp(value: Long) = setState { copy(timeStamp = value) }

    fun resetState() {

        setState { copy(
            species = initialState.species,
            rarity = initialState.rarity,
            notes = initialState.notes,
            geoLocation = initialState.geoLocation,
            timeStamp = initialState.timeStamp)
        }
    } // resetState

} // ObsItemViewModel