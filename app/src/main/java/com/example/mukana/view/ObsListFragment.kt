package com.example.mukana.view

import android.content.Context
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.example.mukana.R
import com.example.mukana.model.BirdObservation
import com.example.mukana.model.BirdObservationList
import com.example.mukana.viewmodel.ObsListViewModel

/**
 * The main list view of the app.
 */

class ObsListFragment : BaseMvRxFragment() {

    private val listViewModel: ObsListViewModel by activityViewModel(ObsListViewModel::class)

    private var listener: OnListFragmentInteractionListener? = null

    // we need a reference to the adapter in order to manipulate its data set
    private lateinit var recyclerViewAdapter: ObsListRecyclerViewAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)

        // the listening is not being used atm, but I'm keeping
        // these checks as they'd be kept in a real app.
        if (context is OnListFragmentInteractionListener) {

            listViewModel.initDatabase(context.applicationContext)
            listener = context

        } else {
            throw Exception("$context must implement OnListFragmentInteractionListener")
        }
    } // OnAttach

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // the observing should only be set on app start.
        if (savedInstanceState != null) return

        listViewModel.databaseBirdObsList.observe(this, object : Observer<List<BirdObservation>> {

            override fun onChanged(list: List<BirdObservation>?) {

                list ?: return

                // this call messes up the styling of the card view items (resets the bg color to default color).
                // disabling it messes with the viewmodel state, but it doesn't seem to matter, with the way the db
                // has been set up; everything still works correctly without this.
                // listViewModel.replaceViewModelList(list)

                // this little line cost me four hours of work. something is off about the combo
                // of MvRx and LiveData; this operation should not be necessary.
                // as it is, without this call, the list view fails to populate when first opening the app.
                recyclerViewAdapter.setList(BirdObservationList(list))
            } // onChanged
        }) // observe
    } // onCreate

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_obslistitem_list, container, false)

        if (view is RecyclerView) {
            with(view) {

                layoutManager = LinearLayoutManager(context)
                setHasFixedSize(true)

                withState(listViewModel) {

                    recyclerViewAdapter = ObsListRecyclerViewAdapter(it, listener)
                    adapter = recyclerViewAdapter
                }
            }
        } // if
        return view
    } // onCreateView

    override fun onDetach() {
        super.onDetach()

        listener = null
    }

    // MvRx requires it. in retrospect, adopting that framework was a mistake,
    // but it's too late to get rid of it now.
    override fun invalidate() {}

    // for communication with MainActivity (not being used atm)
    interface OnListFragmentInteractionListener {

        fun onListFragmentInteraction(item: BirdObservation)
    }

} // ObsListFragment
