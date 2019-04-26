package com.example.mukana.view

import android.graphics.BitmapFactory
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.mukana.R
import com.example.mukana.log
import com.example.mukana.model.*
import com.example.mukana.view.ObsListFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.fragment_obslistitem.view.*

/**
 * [RecyclerView.Adapter] that can display an item and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class ObsListRecyclerViewAdapter(
    private var obsList: BirdObservationList,
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

        val rarityBg = when(item.rarity) {

            // i'm pretty sure there's a better way to do this, but ehh, it works
            Rarity.COMMON -> holder.view.resources.getDrawable(R.drawable.card_view_item_bg_common)
            Rarity.RARE -> holder.view.resources.getDrawable(R.drawable.card_view_item_bg_rare)
            Rarity.EXTREMELY_RARE -> holder.view.resources.getDrawable(R.drawable.card_view_item_bg_extreme)
        }
        
        holder.apply {

            rarityTextView.text = valueToUIString(item.rarity, Accessing.RARITY)
            rarityTextView.background = rarityBg
            speciesTextView.text = valueToUIString(item.species, Accessing.SPECIES) // for consistency (it could be the plain value)
            dateTextView.text = valueToUIString(item.timeStamp, Accessing.TIMESTAMP)
            notesTextView.text = valueToUIString(item.notes, Accessing.NOTES)
            geoLocTextView.text = valueToUIString(item.geoLocation, Accessing.GEOLOC)
            val imagePath = valueToUIString(item.imagePath, Accessing.IMAGE_PATH)

            if (imagePath != "") {

                imageView.setImageBitmap(BitmapFactory.decodeFile(imagePath))
                imageView.visibility = View.VISIBLE
            }

            if (position % 2 == 0) {

                // for better visual flow, give every other card a different bg color
                cardView.setCardBackgroundColor(cardView.resources.getColor(R.color.colorCardAlternate))
                // fun fact: if you call setBackgroundColor instead (as you well might), it removes your corner radius
                // and you can't set it back programmatically (not easily at least; 'cardView.radius = x' has no effect).
            }
        } // apply
        with(holder.view) {

            tag = item
            setOnClickListener(onClickListener)
        }
    } // onBindViewHolder

    internal fun setList(newList: BirdObservationList) {

        obsList = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = obsList.items.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val rarityTextView: TextView = view.rarity
        val speciesTextView: TextView = view.species
        val dateTextView: TextView = view.date
        val notesTextView: TextView = view.notes
        val geoLocTextView: TextView = view.geoloc
        val imageView: ImageView = view.imageView
        val cardView: CardView = view.cardView
    } // ViewHolder

} // ObsListRecyclerViewAdapter
