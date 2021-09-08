package com.anirudroidz.currentweatherforcast

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.anirudroidz.currentweatherforcast.ui.main.MainViewModel
import java.util.concurrent.TimeUnit

class OpenWeatherWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val viewModel: MainViewModel
) : Worker(context, workerParameters) {

    override fun doWork(): Result {
        viewModel.fetchData()
        return Result.success()
    }

}
