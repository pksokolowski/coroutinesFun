package com.github.pksokolowski.coroutinesfun.features.standalones

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.pksokolowski.coroutinesfun.R
import com.github.pksokolowski.coroutinesfun.databinding.FragmentStandalonesBinding
import com.github.pksokolowski.coroutinesfun.utils.textChanges
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

@FlowPreview
@ExperimentalCoroutinesApi
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

        binding.input.textChanges()
            .distinctUntilChanged()
            .filterNotNull()
            .debounce(300)
            .map { it.toString() }
            .onEach(::handleCommand)
            .launchIn(lifecycleScope)

        displayAllKnownCommands()
        binding.input.requestFocus()

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
    )

    fun displayString(@StringRes content: Int) = displayString(getString(content))

    private fun displayString(content: String) {
        binding.output.text = content
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
        val command = commands[input]
        if (command == null) {
            displayAllKnownCommands()
            return
        }
        command()
    }
}