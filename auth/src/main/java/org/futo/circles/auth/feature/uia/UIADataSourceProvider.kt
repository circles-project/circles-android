package org.futo.circles.auth.feature.uia

import org.futo.circles.auth.model.UIAFlowType


object UIADataSourceProvider {

    private var activeFlowDataSource: UIADataSource? = null

    var activeFlowType: UIAFlowType? = null
        private set

    fun getDataSourceOrThrow() =
        activeFlowDataSource ?: throw IllegalArgumentException("Flow is not active")


    fun create(flowType: UIAFlowType, factory: UIADataSource.Factory): UIADataSource {
        activeFlowType = flowType
        return factory.create(flowType).also { activeFlowDataSource = it }
    }

    fun clear() {
        activeFlowType = null
        activeFlowDataSource = null
    }
}
