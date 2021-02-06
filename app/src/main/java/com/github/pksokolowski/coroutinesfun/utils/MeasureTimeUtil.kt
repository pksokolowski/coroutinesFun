package com.github.pksokolowski.coroutinesfun.utils

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

suspend fun measureCoroutineTimeMillis(block: CoroutineScope.() -> Unit): Long =
    withContext(Dispatchers.Main) {
        measureTimeMillis {
            coroutineScope {
                block()
            }
        }
    }