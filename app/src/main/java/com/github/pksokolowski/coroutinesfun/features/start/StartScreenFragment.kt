package com.github.pksokolowski.coroutinesfun.features.start

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.github.pksokolowski.coroutinesfun.R
import com.github.pksokolowski.coroutinesfun.databinding.FragmentStartScreenBinding

class StartScreenFragment(
    private val someDependency: String
) : Fragment() {

    private var _binding: FragmentStartScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.flowsButton.setOnClickListener {
            findNavController().navigate(R.id.action_startScreenFragment_to_flowsFragment)
        }

        binding.downloadsButton.setOnClickListener {
            findNavController().navigate(R.id.action_startScreenFragment_to_downloadsFragment)
        }

        binding.button.setOnClickListener {
            findNavController().navigate(R.id.action_startScreenFragment_to_standAlonesFragment)
        }
    }
}