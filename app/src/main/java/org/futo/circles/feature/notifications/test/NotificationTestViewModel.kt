package org.futo.circles.feature.notifications.test

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.feature.notifications.NotificationTestsProvider
import org.futo.circles.feature.notifications.test.task.TestPushClicker
import org.futo.circles.feature.notifications.test.task.TestPushDisplayEvenReceiver
import org.futo.circles.model.NotificationTestListItem

class NotificationTestViewModel(
    testProvider: NotificationTestsProvider
) : ViewModel() {

    val testsLiveData = MutableLiveData<List<NotificationTestListItem>>()
    private val testList = testProvider.getTestsList()

    init {
        setupTests()
    }

    private fun setupTests() {
        testsLiveData.value = testList.map { it.toListItem() }
        testList.forEach {
            it.runTest { updateTestInfo(it) }
        }
    }

    private fun updateTestInfo(item: NotificationTestListItem) {
        val list = testsLiveData.value?.toMutableList() ?: mutableListOf()
        val index = list.indexOfFirst { it.id == item.id }.takeIf { it != -1 } ?: return
        list[index] = item
        testsLiveData.value = list
    }

    fun onTestPushReceived() {
        testList.filterIsInstance<TestPushDisplayEvenReceiver>()
            .forEach { it.onTestPushDisplayed() }
    }

    fun onTestPushClicked() {
        testList.filterIsInstance<TestPushClicker>().forEach { it.onTestPushClicked() }
    }

    fun onFixNotificationTest(id: Int) {
        testList.firstOrNull { it.titleResId == id }?.runFix()
    }
}