package com.github.pksokolowski.coroutinesfun.features.work

import androidx.recyclerview.widget.DiffUtil
import com.github.pksokolowski.coroutinesfun.model.PrimeCandidate

class PrimeCandidatesDiffCallback(
    private val oldList: List<PrimeCandidate>,
    private val newList: List<PrimeCandidate>
) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}