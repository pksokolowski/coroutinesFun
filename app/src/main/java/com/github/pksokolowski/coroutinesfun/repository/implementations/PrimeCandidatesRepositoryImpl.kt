package com.github.pksokolowski.coroutinesfun.repository.implementations

import com.github.pksokolowski.coroutinesfun.db.dao.PrimeCandidateDao
import com.github.pksokolowski.coroutinesfun.db.dto.PrimeCandidateDto
import com.github.pksokolowski.coroutinesfun.model.PrimeCandidate
import com.github.pksokolowski.coroutinesfun.repository.PrimeCandidatesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import java.math.BigInteger

class PrimeCandidatesRepositoryImpl(
    private val primeCandidatesDao: PrimeCandidateDao
) : PrimeCandidatesRepository {
    override val primeCandidates = primeCandidatesDao.getAllPrimeCandidates()
        .map { dtoList ->
            dtoList.map { PrimeCandidate(it.id, it.candidate, it.isPrime) }
        }
        .flowOn(Dispatchers.Default)

    override suspend fun getUnhandledCandidates(): List<PrimeCandidate> =
        primeCandidatesDao.getAllUnhandledCandidates()
            .map {
                PrimeCandidate(it.id, it.candidate, it.isPrime)
            }

    override suspend fun insertCandidate(number: BigInteger) {
        val candidateDto = PrimeCandidateDto(0, number, null)
        primeCandidatesDao.insertCandidate(candidateDto)
    }

    override suspend fun updateCandidate(candidate: PrimeCandidate) {
        val dto = PrimeCandidateDto(candidate.id, candidate.number, candidate.isPrime)
        primeCandidatesDao.updateCandidate(dto)
    }
}