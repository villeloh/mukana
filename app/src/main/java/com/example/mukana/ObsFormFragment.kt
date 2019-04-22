package com.example.mukana

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_obsform.*

class ObsFormFragment : BaseMvRxFragment(), AdapterView.OnItemSelectedListener {

    // view models keep track of app state even if the fragment is destroyed
    private val itemViewModel: ObsItemViewModel by fragmentViewModel(ObsItemViewModel::class)
    private val listViewModel: ObsListViewModel by activityViewModel(ObsListViewModel::class)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_obsform, container, false)
    } // onCreateView

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        hideFloatingActionButton()
        initRaritySpinner()
    } // onActivityCreated

    private fun onCancelButtonClick() {

        // on cancel, clear the old values. some users might not prefer it, but hey, sucks to be them! :)
        itemViewModel.resetState()
    }

    private fun onCreateButtonClick() {

        withState(itemViewModel) {stateBirdObs -> {

            listViewModel.addListItem(stateBirdObs) // auto-updates the listView
        }}
    }

    // i.e., a spinner item
    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {

        val rarityAsString = parent.getItemAtPosition(pos).toString()
        // to deal with the lack of underscore in the user-visible text. not ideal, but i's the simplest way i can think of.
        val rarity = if (rarityAsString == "EXTREMELY RARE") Rarity.EXTREMELY_RARE else Rarity.valueOf(rarityAsString)

        itemViewModel.updateState(ObsItemViewModel.Updating.RARITY, rarity)
    } // onItemSelected

    override fun onNothingSelected(parent: AdapterView<*>) {
        // Another interface callback
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
            raritySpinner.onItemSelectedListener = this
        }
    } // initRaritySpinner

    private fun setInitialValues() {
        //TODO: set the initial field values from the itemViewModel state
    }

    // called automatically on viewmodel state updates by MvRx
    override fun invalidate() {

        // logically, it should NOT be called in this fragment, as the form should persist until
        // it's manually exited. not sure how to prevent it... will have to see how this works.
    }

} // ObsFormFragment