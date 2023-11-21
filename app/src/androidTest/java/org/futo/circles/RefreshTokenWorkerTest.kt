package org.futo.circles


import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Observer
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
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
import org.futo.circles.auth.feature.token.RefreshTokenWorker
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.math.max

class RefreshTokenWorkerTest {

    lateinit var context: Context

    private val job = SupervisorJob()
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(job + testDispatcher)

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

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
        val workManager = WorkManager.getInstance(context)
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
            ?: throw IllegalArgumentException("test driver is null")


        val expireTime = 3600000L

        val sessionIdData = Data.Builder()
            .putString(RefreshTokenWorker.SESSION_ID_PARAM_KEY, "test")
            .build()

        val flex = max(expireTime / 3, PeriodicWorkRequest.MIN_PERIODIC_FLEX_MILLIS)

        val refreshRequest = PeriodicWorkRequestBuilder<RefreshTokenWorker>(
            expireTime, TimeUnit.MILLISECONDS,
            flex, TimeUnit.MILLISECONDS
        )
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .setInputData(sessionIdData)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "test",
            ExistingPeriodicWorkPolicy.UPDATE,
            refreshRequest
        ).result.get()

        testDriver.setPeriodDelayMet(refreshRequest.id)
        testDriver.setAllConstraintsMet(refreshRequest.id)
        val isSucceeded = awaitForWork(workManager, refreshRequest.id)
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
