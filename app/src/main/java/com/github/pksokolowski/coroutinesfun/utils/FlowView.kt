package com.github.pksokolowski.coroutinesfun.utils

import android.widget.Button
import android.widget.EditText
import androidx.annotation.CheckResult
import androidx.core.widget.doOnTextChanged
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
@CheckResult
fun EditText.textChanges(): Flow<CharSequence?> {
    return callbackFlow {
        val watcher = doOnTextChanged { text, start, count, after ->
            offer(text)
        }
        awaitClose { removeTextChangedListener(watcher) }
    }
}

@ExperimentalCoroutinesApi
@CheckResult
fun EditText.textChangesWithSuggestions(suggestions: List<String>): Flow<String> {
    val initialsToSuggestionsMap = hashMapOf<String, String>()
    val usedKeys = hashSetOf<String>()
    val editText = this
    var previousInput: String? = null
    var lastResolvedInput: String? = null

    // prevent other suggesting popping-up on a valid, complete input.
    suggestions.forEach {
        usedKeys.add(it)
    }

    // not necessarily performant, but interesting to use sequences next to coroutine flows
    suggestions.asSequence()
        .filterNot { it.isEmpty() }
        .flatMap { suggestion ->
            val subStrings = mutableListOf<String>()
            for (i in 1 until suggestion.length) {
                val candidateKey = suggestion.substring(0, i)
                if (usedKeys.contains(candidateKey)) continue
                subStrings.add(candidateKey)
            }
            subStrings.asSequence()
                .map { it to suggestion }
        }
        .forEach {
            val (key, value) = it
            usedKeys.add(key)
            initialsToSuggestionsMap[key] = value
        }

    return callbackFlow {
        var listenerActive = true

        fun withListenerInactive(body: () -> Unit) {
            listenerActive = false
            body()
            listenerActive = true
        }

        val watcher = doOnTextChanged { text, start, count, after ->
            if (!listenerActive) return@doOnTextChanged
            val input = text.toString()
            val suggestion = initialsToSuggestionsMap[input]
            if (suggestion != null && input != lastResolvedInput && input.length > previousInput?.length ?: 0) {
                withListenerInactive {
                    editText.setText(suggestion)
                    editText.setSelection(input.length, suggestion.length)
                    offer(suggestion)
                    lastResolvedInput = input
                }
            } else {
                offer(input)
            }
            previousInput = input
        }
        awaitClose { removeTextChangedListener(watcher) }
    }
}

@ExperimentalCoroutinesApi
@CheckResult
fun Button.clicks(): Flow<Unit> {
    return callbackFlow {
        setOnClickListener { _ ->
            offer(Unit)
        }
        awaitClose { setOnClickListener(null) }
    }
}