package com.github.pksokolowski.coroutinesfun.features.flows

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.pksokolowski.coroutinesfun.databinding.FragmentFlowsBinding
import com.github.pksokolowski.coroutinesfun.utils.observe
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class FlowsFragment : Fragment() {
    private val viewModel: FlowsViewModel by viewModels()

    private val startedScope = CoroutineScope(Dispatchers.Main.immediate)

    private val sharedFlow = MutableSharedFlow<Int>()
    private var nextItemNumber = 0

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

    override fun onStart() {
        super.onStart()
        observeEmissionsToParallelStream()
    }

    override fun onStop() {
        super.onStop()
        startedScope.coroutineContext.cancelChildren()
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

    @Suppress("BlockingMethodInNonBlockingContext")
    private fun observeEmissionsToParallelStream() {
        sharedFlow
            .flatMapMerge(Runtime.getRuntime().availableProcessors()) { item ->
                flow {
                    Thread.sleep(1000)
                    emit(item)
                }
            }
            // this is for experimental purposes (non-main thread work in presentation layer)
            // usually not necessarily the best practice
            .flowOn(Dispatchers.Default)
            .onEach { binding.parallelFlowOutput.text = "$it" }
            .launchIn(startedScope)
    }

    private fun observeEventsAndState() {
        viewModel.event.observe(viewLifecycleOwner) {
            output("observed event")
        }

        viewModel.state.observe(viewLifecycleOwner) {
            output("observed state")
        }

        // below two ways of approaching flows in a lifecycle-aware manner are presented

        lifecycleScope.observe(viewModel.singleEvent) {
            output("observed single event")
        }

        observe(viewModel.altSingleEvent) {
            output("observed alt single event")
        }
    }

    private fun removeEventsAndStateObservers() {
        viewModel.event.removeObservers(viewLifecycleOwner)
        viewModel.state.removeObservers(viewLifecycleOwner)
        lifecycleScope.coroutineContext.cancelChildren()
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

        binding.sendAlternativeSingleEvent.setOnClickListener {
            viewModel.sendAltSingleEvent("alt single event")
        }

        binding.reSubscribe.setOnClickListener {
            binding.output.text = ""
            output("simulating view re-creation")
            removeEventsAndStateObservers()
            observeEventsAndState()
        }

        binding.emit.setOnClickListener {
            lifecycleScope.launchWhenStarted {
                sharedFlow.emit(nextItemNumber++)
            }
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