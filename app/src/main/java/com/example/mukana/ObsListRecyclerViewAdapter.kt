package com.example.mukana

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.mukana.model.BirdObservation
import com.example.mukana.model.BirdObservationList

import com.example.mukana.view.ObsListFragment.OnListFragmentInteractionListener

import kotlinx.android.synthetic.main.fragment_obslistitem.view.*

/**
 * [RecyclerView.Adapter] that can display an item and makes a call to the
 * specified [OnListFragmentInteractionListener].
 * TODO: Replace the implementation with code for your data type.
 */
class ObsListRecyclerViewAdapter(
    private val obsList: BirdObservationList,
    private val mListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<ObsListRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { view ->
            val item = view.tag as BirdObservation
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            mListener?.onListFragmentInteraction(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_obslistitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = obsList.items[position]
        holder.apply {
            rarityTextView.text = item.UI.rarity
            speciesTextView.text = item.UI.species
            dateTextView.text = item.UI.timeStamp
            notesTextView.text = item.UI.notes
            geoLocTextView.text = item.UI.geoLoc
        }
        with(holder.view) {
            tag = item
            setOnClickListener(onClickListener)
        }
    } // onBindViewHolder

    override fun getItemCount(): Int = obsList.items.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val rarityTextView: TextView = view.rarity
        val speciesTextView: TextView = view.species
        val dateTextView: TextView = view.date
        val notesTextView: TextView = view.notes
        val geoLocTextView: TextView = view.geoloc
    } // ViewHolder

} // ObsListRecyclerViewAdapter
