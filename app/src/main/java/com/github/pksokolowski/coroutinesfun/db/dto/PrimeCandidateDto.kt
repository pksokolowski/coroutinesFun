package com.github.pksokolowski.coroutinesfun.db.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigInteger

@Entity(tableName = "prime_candidates")
data class PrimeCandidateDto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "number")
    val candidate: BigInteger,

    @ColumnInfo(name = "is_prime")
    val isPrime: Boolean?
)