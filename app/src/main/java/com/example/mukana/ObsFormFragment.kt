package com.example.mukana

import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.fragmentViewModel

class ObsFormFragment : BaseMvRxFragment() {

    private val viewModel: ObsListViewModel by fragmentViewModel(ObsListViewModel::class)


    override fun invalidate() {
    }
} // ObsFormFragment