package com.example.mukana

import com.airbnb.mvrx.MvRxState

data class BirdObservationList(val items: List<BirdObservation> = emptyList()) : MvRxState