package com.github.pksokolowski.coroutinesfun.features.work

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest

class WorkViewModel @ViewModelInject constructor(
    private val application: Application
) : ViewModel() {
    fun initiateWork() {
        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<SampleCoroutineWorker>()
                .build()

        WorkManager
            .getInstance(application)
            .enqueue(workRequest)

    }
}