package com.example.mukana.viewmodel

import com.example.mukana.model.BirdObservation
import com.example.mukana.model.BirdObservationList

class ObsListViewModel(private val initialState: BirdObservationList) : MvRxViewModel<BirdObservationList>(initialState) {

    /*

    private fun fetchListing() {

        // ListingRequest.forId(1234).execute { copy(listing = it) }
    } */

    // https://github.com/airbnb/MvRx/wiki#updating-state
    fun addItems(newItems: List<BirdObservation>) {

        setState { copy(items = items + newItems) }
    }

    fun addItem(item: BirdObservation) {

        setState { copy(items = items + item) }
    }

    // for clearing all list items. (not user atm)
    fun resetList() {

        setState { copy(items = emptyList()) }
    }

    // we need access to the inner list in order to give it to the RecyclerViewAdapter
    fun list(): BirdObservationList {
        var list = BirdObservationList()
        withState {
            list = it
        }
        return list
    }

} // ObsListViewModel