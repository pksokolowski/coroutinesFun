package com.github.pksokolowski.coroutinesfun.db

import androidx.room.TypeConverter
import java.math.BigDecimal
import java.math.BigInteger

class Converters {
    @TypeConverter
    fun encodeBigInteger(bigInteger: BigInteger): String = bigInteger.toString()

    @TypeConverter
    fun decodeBigInteger(value: String): BigInteger = BigInteger(value)

    @TypeConverter
    fun encodeBigDecimal(bigDecimal: BigDecimal): String = bigDecimal.toString()

    @TypeConverter
    fun decodeBigDecimal(value: String): BigDecimal = BigDecimal(value)
}