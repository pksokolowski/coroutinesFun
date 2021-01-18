package com.github.pksokolowski.coroutinesfun.features.flows

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.pksokolowski.coroutinesfun.databinding.FragmentFlowsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class FlowsFragment : Fragment() {
    private val viewModel: FlowsViewModel by viewModels()
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)

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

    override fun onStop() {
        super.onStop()
        coroutineScope.coroutineContext.cancelChildren()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUserData()
        observeEventsAndState()
        setupOnClickListeners()
    }

    private fun observeEventsAndState() {
        viewModel.event.observe(viewLifecycleOwner) {
            output("observed event")
        }

        viewModel.state.observe(viewLifecycleOwner) {
            output("observed state")
        }

        viewModel.singleEvent
            .onEach { output("observed single event") }
            .launchIn(coroutineScope)
    }

    private fun removeEventsAndStateObservers() {
        viewModel.event.removeObservers(viewLifecycleOwner)
        viewModel.state.removeObservers(viewLifecycleOwner)
        coroutineScope.coroutineContext.cancelChildren()
    }

    private fun output(content: String?) {
        val currentOutput = binding.output.text
        binding.output.text = "$currentOutput\n$content"
    }

    private fun setupOnClickListeners() {
        binding.cycleUserData.setOnClickListener {
            viewModel.loadNextUser()
        }

        binding.sendEvent.setOnClickListener {
            viewModel.sendEvent("event livedata observed")
        }

        binding.setState.setOnClickListener {
            viewModel.setState("some new state observed")
        }

        binding.sendSingleEvent.setOnClickListener {
            viewModel.sendSingleEvent("new single event")
        }

        binding.reSubscribe.setOnClickListener {
            binding.output.text = ""
            output("simulating view re-creation")
            removeEventsAndStateObservers()
            observeEventsAndState()
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