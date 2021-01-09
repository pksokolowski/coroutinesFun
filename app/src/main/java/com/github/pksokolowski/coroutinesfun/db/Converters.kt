package com.github.pksokolowski.coroutinesfun.db

import androidx.room.TypeConverter
import java.math.BigInteger

class Converters {
    @TypeConverter
    fun encodeBigInteger(bigInteger: BigInteger): String = bigInteger.toString()

    @TypeConverter
    fun decodeBigInteger(value: String): BigInteger = BigInteger(value)
}