package com.example.photoplaces.utils

import androidx.annotation.Nullable
import androidx.recyclerview.widget.DiffUtil
import com.example.photoplaces.data.entity.Place

class PlaceDiffCallback(private val oldList: List<Place>, private val newList: List<Place>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return oldList[oldPosition].equals(newList[newPosition])
    }

    @Nullable
    override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
        return super.getChangePayload(oldPosition, newPosition)
    }
}