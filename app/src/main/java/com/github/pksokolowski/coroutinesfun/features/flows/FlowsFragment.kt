package com.github.pksokolowski.coroutinesfun.features.flows

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.pksokolowski.coroutinesfun.databinding.FragmentFlowsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FlowsFragment : Fragment() {
    private val viewModel: FlowsViewModel by viewModels()

    private var _binding: FragmentFlowsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFlowsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUserData()
        setupOnClickListeners()
    }

    private fun setupOnClickListeners() {
        binding.cycleUserData.setOnClickListener {
            viewModel.loadNextUser()
        }
    }

    private fun observeUserData() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            when (user) {
                is Unknown -> {
                    displayUserData("", "", "")
                }
                is LoggedIn -> {
                    val (name, email, address) = user
                    displayUserData(name, address, email)
                }
            }

        }
    }

    private fun displayUserData(name: String, address: String, email: String) {
        binding.userName.text = name
        binding.userAddress.text = address
        binding.userEmail.text = email
    }

}