package com.example.mukana.model

import com.airbnb.mvrx.MvRxState

data class BirdObservationList(val items: List<BirdObservation> = emptyList()) : MvRxState {

    // NOTE#1: rn this is not being used, as the timestamp sorting is 'automatic': time keeps passing,
    // so when new observations are being created, they're always inserted in the correct order.
    // NOTE #2: disabling this for now; MvRx requires that MvRxStates use immutable data, which means
    // that the list can't be sorted in place. i'll have to rethink the logic here.
    /* fun sortByTimeStamp() {

        if (items.isEmpty() || items.size == 1) return

        items.sortBy { it.timeStamp } // ascending order by default
    } */

} // BirdObservationList