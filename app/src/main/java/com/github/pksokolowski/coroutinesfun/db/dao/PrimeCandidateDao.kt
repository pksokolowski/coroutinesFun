package com.github.pksokolowski.coroutinesfun.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.github.pksokolowski.coroutinesfun.db.dto.PrimeCandidateDto
import kotlinx.coroutines.flow.Flow

@Dao
interface PrimeCandidateDao {
    @Query("SELECT * FROM prime_candidates ORDER BY id ASC")
    fun getAllPrimeCandidates(): Flow<List<PrimeCandidateDto>>

    @Query("SELECT * FROM prime_candidates WHERE is_prime = null ORDER BY id ASC")
    suspend fun getAllUnhandledCandidates(): List<PrimeCandidateDto>

    @Insert
    suspend fun insertCandidate(primeCandidateDto: PrimeCandidateDto): Long

    @Update
    suspend fun updateCandidate(primeCandidateDto: PrimeCandidateDto)
}