package com.github.pksokolowski.coroutinesfun.features.work

import android.content.Context
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.github.pksokolowski.coroutinesfun.db.dao.AnimalsDao

class SampleCoroutineWorker @WorkerInject constructor(
    @Assisted application: Context,
    @Assisted params: WorkerParameters,
    animalsDao: AnimalsDao,
) : CoroutineWorker(application, params) {
    override suspend fun doWork(): Result {
        TODO("Not yet implemented")
    }

}