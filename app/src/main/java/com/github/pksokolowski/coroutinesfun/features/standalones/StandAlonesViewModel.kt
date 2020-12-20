package com.github.pksokolowski.coroutinesfun.features.standalones

import androidx.lifecycle.ViewModel
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StandAlonesViewModel @ViewModelInject constructor(

) : ViewModel() {

    private val _output = MutableSharedFlow<String>()
    val output: SharedFlow<String> = _output

    private fun output(content: String) {
        viewModelScope.launch {
            _output.emit(content)
        }
    }

    fun runSomeFunCoroutines() {
        suspend fun findSmallestPrime(): Int {
            delay(1500)
            return 2
        }

        suspend fun writeAPoem(): String {
            delay(500)
            return "Lorem ipsum dolor sit amet"
        }

        viewModelScope.launch {
            val primeDeferred = async { findSmallestPrime() }
            val poemDeferred = async { writeAPoem() }

            output("Started work")

            val prime = primeDeferred.await()
            val poem = poemDeferred.await()

            output(
                """
                got both:
                a poem  = $poem
                and a prime  = $prime
            """.trimIndent()
            )
        }
    }
}