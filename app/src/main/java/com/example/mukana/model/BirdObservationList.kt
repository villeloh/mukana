package com.example.mukana.model

import com.airbnb.mvrx.MvRxState

// upon reflection, I doubt if this class needs to exist at all. It could probably be replaced
// with just a 'naked' list of BirdObservations.
data class BirdObservationList(val items: List<BirdObservation> = emptyList()) : MvRxState