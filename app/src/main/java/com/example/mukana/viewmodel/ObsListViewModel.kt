package com.example.mukana.viewmodel

import com.example.mukana.log
import com.example.mukana.model.BirdObservation
import com.example.mukana.model.BirdObservationList

class ObsListViewModel(private val initialState: BirdObservationList) : MvRxViewModel<BirdObservationList>(initialState) {

    // this has to be done here and not in BirdObservationList itself, since the underlying list object is immutable.
    // NOTE: rn this is not being used, as the timestamp sorting is 'automatic': time keeps passing,
    // so when new observations are being created, they're always inserted in the correct order.
    fun sortByTimeStamp() {

        withState {

            if (it.items.isEmpty() || it.items.size == 1) return@withState

            setState { copy(items = items.sortedByDescending { item -> item.timeStamp }) } // newest first
        }
    } // sortByTimeStamp

    // https://github.com/airbnb/MvRx/wiki#updating-state
    fun addItems(newItems: List<BirdObservation>) {

        log("adding item list: " + newItems.toString())
        setState { copy(items = items + newItems) }
    }

    fun addItem(item: BirdObservation) {

        log("adding birdObs: " + item.toString())
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