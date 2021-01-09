package com.github.pksokolowski.coroutinesfun.features.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.pksokolowski.coroutinesfun.model.PrimeCandidate
import com.github.pksokolowski.coroutinesfun.repository.PrimeCandidatesRepository

class SampleCoroutineWorker @WorkerInject constructor(
    @Assisted application: Context,
    @Assisted params: WorkerParameters,
    private val primeCandidatesRepository: PrimeCandidatesRepository,
) : CoroutineWorker(application, params) {
    override suspend fun doWork(): Result {
        primeCandidatesRepository.getUnhandledCandidates()
            .forEach { candidate ->
                val isPrime = candidate.number.isProbablePrime(100)
                val newValue = PrimeCandidate(candidate.id, candidate.number, isPrime)
                primeCandidatesRepository.updateCandidate(newValue)
            }
        return Result.success()
    }

}