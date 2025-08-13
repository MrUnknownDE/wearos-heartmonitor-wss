package com.mrunk.wearhr

import android.content.Context
import android.util.Log
import androidx.health.services.client.ExerciseClient
import androidx.health.services.client.HealthServices
import androidx.health.services.client.data.DataType
import androidx.health.services.client.data.ExerciseCapabilities
import androidx.health.services.client.data.ExerciseConfig
import androidx.health.services.client.data.ExerciseGoal
import androidx.health.services.client.data.ExerciseType
import androidx.health.services.client.data.HeartRateAccuracy
import androidx.health.services.client.data.IntervalGoal
import androidx.health.services.client.data.SetGoal
import androidx.health.services.client.event.ExerciseUpdate
import androidx.health.services.client.event.ExerciseUpdateListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class Health(private val ctx: Context, private val onHr: (Int) -> Unit) : ExerciseUpdateListener {
    private val scope = CoroutineScope(Dispatchers.Default + Job())
    private val exerciseClient: ExerciseClient = HealthServices.getClient(ctx).exerciseClient

    suspend fun start() {
        val caps: ExerciseCapabilities = exerciseClient.getCapabilitiesAsync().await()
        if (!caps.supportedExerciseTypes.contains(ExerciseType.OTHER_WORKOUT)) {
            Log.w("Health", "Exercise OTHER_WORKOUT not supported; trying without exercise")
        }
        val config = ExerciseConfig(
            exerciseType = ExerciseType.OTHER_WORKOUT,
            dataTypes = setOf(DataType.HEART_RATE_BPM)
        )
        exerciseClient.setUpdateListener(this)
        exerciseClient.startExerciseAsync(config).await()
    }

    suspend fun stop() {
        exerciseClient.pauseExerciseAsync().await()
        exerciseClient.endExerciseAsync().await()
        exerciseClient.clearUpdateListener()
    }

    override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
        val hr = update.latestMetrics[DataType.HEART_RATE_BPM]
        val bpm = hr?.value?.toInt()
        if (bpm != null && bpm > 0) onHr(bpm)
    }
}