package com.github.pksokolowski.coroutinesfun.features.work

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
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
        val constraints = Constraints.Builder()
            .setRequiresDeviceIdle(true)
            .build()

        val workRequest: OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<SampleCoroutineWorker>()
                .setConstraints(constraints)
                .build()

        WorkManager
            .getInstance(application)
            .enqueueUniqueWork(PRIMALITY_TESTS_WORK, ExistingWorkPolicy.REPLACE, workRequest)
    }

    private companion object {
        const val PRIMALITY_TESTS_WORK = "primality tests"
    }
}