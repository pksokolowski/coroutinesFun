package com.github.pksokolowski.coroutinesfun.features.work

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.github.pksokolowski.coroutinesfun.repository.PrimeCandidatesRepository
import kotlinx.coroutines.launch
import java.math.BigInteger

class WorkViewModel @ViewModelInject constructor(
    private val application: Application,
    private val primeCandidatesRepository: PrimeCandidatesRepository
) : ViewModel() {
    val primeCandidates = primeCandidatesRepository.primeCandidates.asLiveData()

    fun insertNewCandidate(candidate: BigInteger) = viewModelScope.launch {
        primeCandidatesRepository.insertCandidate(candidate)
        initiateWork()
    }

    private fun initiateWork() {
        val workRequest: WorkRequest =
            OneTimeWorkRequestBuilder<SampleCoroutineWorker>()
                .build()

        WorkManager
            .getInstance(application)
            .enqueue(workRequest)
    }
}