package com.github.pksokolowski.coroutinesfun.features.standalones

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

/**
 * Separated from Viewmodel to remove background thread work from presentation layer.
 * That is part of the idea to have activities, viewmodels, presenters etc free of
 * background thread work. While this might not necessarily be the case throughout a
 * samples project like this, it's still good to try it out.
 */
class BackgroundWorkUseCase {
    suspend fun computeSomething(input: Int): Int = withContext(Dispatchers.Default) {
        delay(1000)
        input * 4
    }
}