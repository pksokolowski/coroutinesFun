package com.github.pksokolowski.coroutinesfun.features.work

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.pksokolowski.coroutinesfun.R
import com.github.pksokolowski.coroutinesfun.databinding.FragmentWorkBinding
import com.github.pksokolowski.coroutinesfun.utils.clicks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import java.math.BigInteger

@AndroidEntryPoint
@ExperimentalCoroutinesApi
class WorkFragment(

) : Fragment() {
    private val viewModel: WorkViewModel by viewModels()

    private var _binding: FragmentWorkBinding? = null
    private val binding get() = _binding!!

    private val adapter = PrimeCandidatesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCandidateInputHandling()
        setupRecyclerView()
    }

    private fun setupCandidateInputHandling() {
        binding.checkCandidate.clicks()
            .map { binding.newCandidateInput.text.toString() }
            .map { BigInteger(it) }
            .catch {
                binding.newCandidateInput.error =
                    getString(R.string.work_error_input_parsing_failed)
            }
            .onEach { candidate ->
                viewModel.insertNewCandidate(candidate)
                binding.newCandidateInput.text = null
            }
            .launchIn(lifecycleScope)
    }

    private fun setupRecyclerView() {
        viewModel.primeCandidates.observe(viewLifecycleOwner) { candidates ->
            adapter.setItems(candidates)
        }

        binding.primesRecycler.adapter = adapter
        val layoutManager = LinearLayoutManager(activity)
        binding.primesRecycler.layoutManager = layoutManager
    }
}