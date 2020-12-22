package com.github.pksokolowski.coroutinesfun.utils

import android.graphics.Bitmap
import android.graphics.Color

fun Bitmap.filterOutColors(): Bitmap {

    val pixels = IntArray(width * height)
    getPixels(pixels, 0, width, 0, 0, width, height)

    for (y in 0 until height)
        for (x in 0 until width) {
            val i = y * width + x
            val averageChannelValue =
                (Color.red(pixels[i]) + Color.green(pixels[i]) + Color.blue(pixels[i])) / 3

            pixels[i] = Color.rgb(averageChannelValue, averageChannelValue, averageChannelValue)
        }


    return Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565).also {
        it.setPixels(pixels, 0, width, 0, 0, width, height)
    }
}