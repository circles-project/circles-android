package org.futo.circles.core.auth

import androidx.lifecycle.ViewModel

abstract class BaseLoginStagesViewModel(
    loginStagesDataSource: BaseLoginStagesDataSource
) : ViewModel() {

    val subtitleLiveData = loginStagesDataSource.subtitleLiveData
    val loginStageNavigationLiveData = loginStagesDataSource.loginStageNavigationLiveData


}