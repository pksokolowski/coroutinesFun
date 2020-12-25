package com.github.pksokolowski.coroutinesfun.features.persistence

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.pksokolowski.coroutinesfun.R
import com.github.pksokolowski.coroutinesfun.databinding.FragmentPersistenceBinding
import com.github.pksokolowski.coroutinesfun.model.Animal
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersistenceFragment : Fragment() {
    private val viewModel: PersistenceViewModel by viewModels()

    private var _binding: FragmentPersistenceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersistenceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupOnClickListeners()
        setupLoadedAnimalInfoObserver()
    }

    private fun setupLoadedAnimalInfoObserver() {
        viewModel.loadedAnimal.observe(viewLifecycleOwner) { animal ->
            if (animal == null) return@observe
            displayAnimal(animal)
        }
    }

    private fun displayAnimal(animal: Animal) {
        binding.outputAnimalName.text = animal.name
        binding.outputAnimalDescription.text = animal.description
    }

    private fun setupOnClickListeners() {
        binding.loadNextAnimal.setOnClickListener {
            viewModel.loadNext()
        }

        binding.saveAnimal.setOnClickListener {
            withValidatedInput { animal ->
                viewModel.saveAnimal(animal)
                clearInput()
            }
        }
    }

    private fun clearInput() {
        binding.inputAnimalName.text = null
        binding.inputAnimalDescription.text = null
        binding.inputAnimalName.requestFocus()
    }

    private fun withValidatedInput(onValid: (animal: Animal) -> Unit) {
        var isValid = true
        listOf(
            binding.inputAnimalName,
            binding.inputAnimalDescription,
        ).forEach { input ->
            if (input.text.isBlank()) {
                val errorMessage = getString(R.string.persistence_error_required_field)
                input.error = errorMessage
                isValid = false
            }
        }

        if (isValid) {
            val animal = Animal(
                binding.inputAnimalName.text.toString(),
                binding.inputAnimalDescription.text.toString()
            )
            onValid(animal)
        }
    }

}