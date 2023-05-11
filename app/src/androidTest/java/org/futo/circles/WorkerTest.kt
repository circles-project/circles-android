package org.futo.circles

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Observer
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.futo.circles.feature.photos.backup.service.MediaBackupWorker
import org.junit.Before
import org.junit.Test
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume

class WorkerTest {

    lateinit var context: Context

    private val job = SupervisorJob()
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(job + testDispatcher)


    @Before
    fun setup() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }

    @Test
    fun testPeriodicWork() = testScope.runTest {
        val request =
            PeriodicWorkRequestBuilder<MediaBackupWorker>(16, TimeUnit.MINUTES, 6, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                ).build()


        val workManager = WorkManager.getInstance(context)
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
            ?: throw IllegalArgumentException("test driver is null")

        workManager.enqueueUniquePeriodicWork(
            "test_work",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        ).result.get()

        testDriver.setPeriodDelayMet(request.id)
        testDriver.setAllConstraintsMet(request.id)
        val isSucceeded = awaitForWork(workManager, request.id)
        Log.d("MyLog", "assert $isSucceeded")
        assertTrue(isSucceeded)
    }

    private suspend fun awaitForWork(
        workManager: WorkManager,
        workId: UUID
    ): Boolean {
        val checkWorkerLiveState = workManager.getWorkInfoByIdLiveData(workId)

        return suspendCancellableCoroutine {
            val observer = object : Observer<WorkInfo> {
                override fun onChanged(value: WorkInfo) {
                    Log.d("MyLog", value.state.toString())
                    if (value.state == WorkInfo.State.ENQUEUED) {
                        checkWorkerLiveState.removeObserver(this)
                        it.resume(true)
                    }
                    if (value.state == WorkInfo.State.FAILED || value.state == WorkInfo.State.CANCELLED) {
                        checkWorkerLiveState.removeObserver(this)
                        it.resume(false)
                    }
                }
            }
            Handler(Looper.getMainLooper()).post { checkWorkerLiveState.observeForever(observer) }
        }
    }

}
