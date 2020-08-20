package com.example.photoplaces.ui.places

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.photoplaces.R
import com.example.photoplaces.data.entity.Place
import com.example.photoplaces.databinding.PlaceListItemBinding

class PlacesListAdapter(private val placeItemClickListener: PlaceItemClickListener, var places: MutableList<Place>) : RecyclerView.Adapter<PlacesListAdapter.PlacesViewHolder>() {

    class PlacesViewHolder(val view: PlaceListItemBinding) : RecyclerView.ViewHolder(view.root)

    fun setDataSource(dataSource: List<Place>?) {
        dataSource?.let {ds ->
            val pos = places.size
            val count = ds.size
            places.addAll(ds)

            notifyItemRangeInserted(pos, count)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = DataBindingUtil.inflate<PlaceListItemBinding>(inflater, R.layout.place_list_item, parent, false)

        return PlacesViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        holder.view.place = places[position]
        holder.view.listener = placeItemClickListener
    }

    override fun getItemId(index: Int): Long {
        return getItemId(index)
    }

    override fun getItemCount(): Int = places.size

}

