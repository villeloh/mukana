package com.example.mukana

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.activityViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_obsform.*

class ObsFormFragment : BaseMvRxFragment() {

    private val viewModel: ObsListViewModel by activityViewModel(ObsListViewModel::class)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_obsform, container, false)
    } // onCreateView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        hideFloatingActionButton()
        initRaritySpinner()
    } // onActivityCreated

    private fun onCancelButtonClick() {

    }

    private fun onCreateButtonClick() {


        // viewModel.addListItem()
    }

    private fun hideFloatingActionButton() {

        activity!!.floatingActionButton.hide() // it's part of the activity (not sure if this is kosher, but it seems to work)
    }

    private fun initRaritySpinner() {

        ArrayAdapter.createFromResource(
            context!!, // there is always a context at this point; not sure why it needs this
            R.array.rarity_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            raritySpinner.adapter = adapter
        }
    } // initRaritySpinner

    // called automatically on viewmodel state updates by MvRx
    override fun invalidate() {

        // it should NOT be called in this fragment, as the form should exist until
        // it's manually exited. but MvRx requires that I override it anyway.
    }

} // ObsFormFragment