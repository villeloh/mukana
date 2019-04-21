package com.example.mukana

import com.airbnb.mvrx.BaseMvRxViewModel
import com.airbnb.mvrx.MvRxState

// the debugMode setup is required by MvRx, but it can only be set from an actual application, not from the library itself.
// therefore we need to make our own abstract base class for our view models, as per the MvRx docs.
abstract class MvRxViewModel<S : MvRxState>(initialState: S) : BaseMvRxViewModel<S>(initialState, debugMode = BuildConfig.DEBUG)