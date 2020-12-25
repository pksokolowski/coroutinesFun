package com.github.pksokolowski.coroutinesfun.features.persistence

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pksokolowski.coroutinesfun.model.Animal
import com.github.pksokolowski.coroutinesfun.repository.AnimalsRepository
import com.github.pksokolowski.coroutinesfun.utils.toMutableCyclicIterator
import kotlinx.coroutines.launch

class PersistenceViewModel @ViewModelInject constructor(
    private val animalsRepository: AnimalsRepository
) : ViewModel() {

    private val _loadedAnimal = MutableLiveData<Animal?>()
    val loadedAnimal: LiveData<Animal?> = _loadedAnimal

    private var knownAnimalsIds = listOf<Long>().toMutableCyclicIterator()

    init {
        loadNext()
    }

    fun loadNext() {
        viewModelScope.launch {
            if (!knownAnimalsIds.hasNext()) {
                loadKnownIds()
            }
            if (knownAnimalsIds.hasNext()) {
                knownAnimalsIds.next().let { id ->
                    _loadedAnimal.value = animalsRepository.getAnimalById(id)
                }
            }
        }
    }

    private suspend fun loadKnownIds() {
        animalsRepository
            .getAllKnownAnimalsIds()
            .let { knownAnimalsIds.replaceData(it) }
    }

    fun saveAnimal(animal: Animal) {
        viewModelScope.launch {
            animalsRepository.saveAnimal(animal)
            loadKnownIds()
        }
    }
}