package com.example.mukana

class ObsListViewModel(val initialState: BirdObservationList) : MvRxViewModel<BirdObservationList>(initialState) {

    init {
        fetchListing()
    }

    private fun fetchListing() {

        // ListingRequest.forId(1234).execute { copy(listing = it) }
    }

    // https://github.com/airbnb/MvRx/wiki#updating-state
    fun addListItem(item: BirdObservation) {

        setState { copy(items = items + item) }
    }

} // ObsListViewModel