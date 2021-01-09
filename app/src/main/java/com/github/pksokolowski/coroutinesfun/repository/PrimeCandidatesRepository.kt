package com.github.pksokolowski.coroutinesfun.repository

import com.github.pksokolowski.coroutinesfun.model.PrimeCandidate
import kotlinx.coroutines.flow.Flow
import java.math.BigInteger

interface PrimeCandidatesRepository {
    val primeCandidates: Flow<List<PrimeCandidate>>

    suspend fun insertCandidate(number: BigInteger)
    suspend fun getUnhandledCandidates(): List<PrimeCandidate>
    suspend fun updateCandidate(candidate: PrimeCandidate)
}