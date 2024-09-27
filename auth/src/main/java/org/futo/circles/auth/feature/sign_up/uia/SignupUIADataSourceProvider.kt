package org.futo.circles.auth.feature.sign_up.uia

import android.content.Context
import org.matrix.android.sdk.api.auth.registration.Stage

object SignupUIADataSourceProvider {

    private var instance: SignupUIADataSource? = null

    fun getDataSourceOrThrow() =
        instance ?: throw IllegalArgumentException("Signup is not started")


    fun create(
        context: Context,
        homeServerUrl: String,
        stages: List<Stage>
    ): SignupUIADataSource {
        val dataSource = SignupUIADataSource(context).also { instance = it }
        dataSource.startSignupUIAStages(homeServerUrl, stages)
        return dataSource
    }

    fun clear() {
        instance = null
    }
}