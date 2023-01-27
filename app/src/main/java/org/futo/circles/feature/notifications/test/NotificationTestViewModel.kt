package org.futo.circles.feature.notifications.test

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.feature.notifications.NotificationTestsProvider
import org.futo.circles.feature.notifications.test.task.BaseNotificationTest

class NotificationTestViewModel(
    private val testProvider: NotificationTestsProvider
) : ViewModel() {

    val testsLiveData = MutableLiveData<BaseNotificationTest>()

    init {
        setupTests()
    }

    private fun setupTests() {

    }
}