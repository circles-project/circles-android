package org.futo.circles.auth.feature.uia

enum class UIAFlowType { Login, Signup, ReAuth, ForgotPassword }

object UIADataSourceProvider {

    var activeFlowDataSource: UIADataSource? = null
        private set

    var activeFlowType: UIAFlowType? = null
        private set

    fun getDataSourceOrThrow() =
        activeFlowDataSource ?: throw IllegalArgumentException("Flow is not active")


    fun startUIAFlow(flowType: UIAFlowType, factory: UIADataSource.Factory) {
        activeFlowType = flowType
        activeFlowDataSource = factory.create(flowType)

    }
}
