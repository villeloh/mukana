package com.example.mukana.view

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.BaseMvRxFragment
import com.airbnb.mvrx.activityViewModel
import com.airbnb.mvrx.withState
import com.example.mukana.ObsListRecyclerViewAdapter
import com.example.mukana.R
import com.example.mukana.log
import com.example.mukana.model.BirdObservation
import com.example.mukana.model.BirdObservationList
import com.example.mukana.model.Rarity
import com.example.mukana.viewmodel.ObsListViewModel
import kotlinx.android.synthetic.main.activity_main.*
import java.sql.Timestamp

/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [ObsListFragment.OnListFragmentInteractionListener] interface.
 */
class ObsListFragment : BaseMvRxFragment() {

    private val tempList = listOf(
        BirdObservation(
            "lintu",
            Rarity.COMMON,
            "note",
            Location(""),
            System.currentTimeMillis()
        ),
        BirdObservation(
            "lintu2",
            Rarity.RARE,
            "notezzzz",
            Location(""),
            System.currentTimeMillis()
        )
    )

    private val viewModel: ObsListViewModel by activityViewModel(ObsListViewModel::class)

    // TODO: Customize parameters
    private var columnCount = 1

    private var listener: OnListFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // must be added here (instead of onCreateView), due to the async nature of the state management
        // viewModel.addItems(tempList)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_obslistitem_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                layoutManager = when {
                    columnCount <= 1 -> LinearLayoutManager(context)
                    else -> GridLayoutManager(context, columnCount)
                }
                setHasFixedSize(true)
                withState(viewModel) {

                    // log(viewModel.toString())
                    // in order to get the correct view model state, we need the withState block
                    adapter = ObsListRecyclerViewAdapter(it, listener)
                }
            }
        } // if
        return view
    } // onCreateView

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()

        listener = null
    }

    override fun invalidate() {

    }

    interface OnListFragmentInteractionListener {

        fun onListFragmentInteraction(item: BirdObservation)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            ObsListFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    } // companion object

} // ObsListFragment
