package com.github.pksokolowski.coroutinesfun.features.standalones

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.pksokolowski.coroutinesfun.R
import com.github.pksokolowski.coroutinesfun.databinding.FragmentStandalonesBinding
import com.github.pksokolowski.coroutinesfun.features.standalones.StandAlonesViewModel.ParallelExecMode
import com.github.pksokolowski.coroutinesfun.utils.textChangesWithSuggestions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.flow.*

@ExperimentalStdlibApi
@ObsoleteCoroutinesApi
@FlowPreview
@ExperimentalCoroutinesApi
@AndroidEntryPoint
class StandAlonesFragment : Fragment() {

    private val viewModel: StandAlonesViewModel by viewModels()

    private var _binding: FragmentStandalonesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStandalonesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.input.textChangesWithSuggestions(commands.keys.toList())
            .filterNotNull()
            .debounce(300)
            .onEach(::handleCommand)
            .launchIn(lifecycleScope)

        displayAllKnownCommands()
        binding.input.requestFocus()

        binding.output.movementMethod = ScrollingMovementMethod()

        observeViewModelOutput()
    }

    private fun observeViewModelOutput() {
        lifecycleScope.launchWhenCreated {
            viewModel.output.collect { message ->
                displayString(message)
            }
        }
    }

    private val commands = hashMapOf(
        "" to ::displayAllKnownCommands,
        "simple1" to { viewModel.runSomeFunCoroutines() },
        "fakenet" to { viewModel.runHandleExceptions(false) },
        "fakenet-exception" to { viewModel.runHandleExceptions((true)) },
        "exceptions and cancellation" to { viewModel.exceptionsAndCancellation() },
        "supervisorJob" to { viewModel.exceptionsAndSupervisorJob() },
        "supervisorJobAsync" to { viewModel.exceptionsAndSupervisorJobWithDeferred() },
        "try-catch exceptions and cancellation" to { viewModel.tryCatchExceptionsAndCancellation() },
        "timeout" to { viewModel.withTimeoutSample() },
        "transform" to { viewModel.transformSample() },
        "buffer" to { viewModel.bufferSample(true) },
        "buffer-off" to { viewModel.bufferSample(false) },
        "channel" to { viewModel.produceChannelSample() },
        "fan-out" to { viewModel.fanOutSample(10) },
        "fan-out-2" to { viewModel.fanOutSample(2) },
        "shared-res" to { viewModel.sharedResourceAccessSample(false) },
        "shared-res-mutex" to { viewModel.sharedResourceAccessSample(true) },
        "combine" to { viewModel.combineLatestSample() },
        "default" to { viewModel.handleErrorWithDefaultSample() },
        "fallback" to { viewModel.handleErrorOnErrorSwitchToAlternativeSolution(1) },
        "retry" to { viewModel.handleErrorsRetry() },
        "cancellation" to { viewModel.handleCancellation() },
        "backpressure" to { viewModel.backPressure() },
        "conflate" to { viewModel.conflateSample() },
        "custom1" to { viewModel.customOperatorSimple() },
        "doubletap" to { viewModel.customOperatorDoubleClick() },
        "shared-late" to { viewModel.lateToSharedFlow() },
        "shared-across" to { viewModel.sharedFlowFromAnotherCoroutineScope() },
        "shared-same" to { viewModel.sharedFlowFromAnotherCoroutineScope(true) },
        "shared-2" to { viewModel.secondSubscriberOfSharedFlow() },
        "shared-drop-oldest" to { viewModel.sharedWithoutBackpressure() },
        "flatMapMerge" to { viewModel.flatMapMerge() },
        "execution-time" to { viewModel.executionTime() },
        "background-work" to { viewModel.runBackgroundWork() },
        "main-immediate" to { viewModel.mainImmediate() },
        "built-in cooperative cancellation" to { viewModel.builtInCooperation() },
        "adapt-one-shot-operation-to-suspend-fun" to { viewModel.oneShotToSuspend() },
        "non-cancellable" to { viewModel.nonCancellable() },
        "stress-single-flow-event" to { viewModel.stressSingleFlowEvent() },
        "leak a job" to { viewModel.simpleLeakTest() },
        "new-scope/job vs new-coroutine" to { viewModel.newScopeVsNewCoroutine() },
        "yield" to { viewModel.yieldSample() },
        "dispatchers" to { viewModel.dispatchersComparison() },
        "structured-concurrency" to { viewModel.structuredConcurrencyTest() },
        "parallel-perf-flow" to { viewModel.parallelExecutionPerf(ParallelExecMode.FLOW) },
        "parallel-perf-fanout" to { viewModel.parallelExecutionPerf(ParallelExecMode.FAN_OUT) },
        "parallel-perf-launch" to { viewModel.parallelExecutionPerf(ParallelExecMode.LAUNCH_INDEPENDENTLY) },
    )

    @SuppressLint("SetTextI18n")
    private fun displayString(content: String) {
        binding.output.text = "${binding.output.text}\n$content"
    }

    private fun displayAllKnownCommands() {
        displayString(
            getString(
                R.string.standalones_known_commands,
                commands.keys.toList().joinToString("\n")
            )
        )
    }

    private fun clearOutput() {
        binding.output.text = ""
    }

    private fun handleCommand(input: String) {
        clearOutput()
        viewModel.cancelSampleJobs()
        val command = commands[input]
        if (command == null) {
            viewModel.cancelSampleJobs()
            displayAllKnownCommands()
            return
        }
        command()
    }
}