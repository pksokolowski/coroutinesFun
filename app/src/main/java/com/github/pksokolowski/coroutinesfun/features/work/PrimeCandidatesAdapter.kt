package com.github.pksokolowski.coroutinesfun.features.work

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.pksokolowski.coroutinesfun.R
import com.github.pksokolowski.coroutinesfun.databinding.ItemPrimeCandidateBinding
import com.github.pksokolowski.coroutinesfun.model.PrimeCandidate

class PrimeCandidatesAdapter : RecyclerView.Adapter<PrimeCandidatesAdapter.ViewHolder>() {
    private var data: MutableList<PrimeCandidate> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_prime_candidate, parent, false)
        return ViewHolder(itemView)
    }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(data[position]) {
            holder.bind(this)
        }
    }

    fun setItems(newData: List<PrimeCandidate>) {
        val diffResult: DiffUtil.DiffResult =
            DiffUtil.calculateDiff(PrimeCandidatesDiffCallback(data, newData.toList()))
        data.clear()
        data.addAll(newData)
        diffResult.dispatchUpdatesTo(this)
    }

    class ViewHolder(
        view: View
    ) : RecyclerView.ViewHolder(view) {
        val binding = ItemPrimeCandidateBinding.bind(view)
        var content: PrimeCandidate? = null

        @SuppressLint("SetTextI18n")
        fun bind(content: PrimeCandidate) {
            this.content = content
            binding.number.text = content.number.toString()
            binding.status.setText(
                when (content.isPrime) {
                    null -> R.string.work_prime_status_unknown
                    true -> R.string.work_prime_status_prime
                    false -> R.string.work_prime_status_composite
                }
            )
        }
    }


}