package com.example.mukana.model

import com.airbnb.mvrx.MvRxState

data class BirdObservationList(val items: List<BirdObservation> = emptyList()) : MvRxState