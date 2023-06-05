package org.futo.circles.auth.feature.reauth

import androidx.lifecycle.ViewModel

class ReAuthStageViewModel(reAuthStagesDataSource: ReAuthStagesDataSource) : ViewModel() {

    val subtitleLiveData = reAuthStagesDataSource.subtitleLiveData
    val loginStageNavigationLiveData = reAuthStagesDataSource.loginStageNavigationLiveData
    val finishReAuthEventLiveData = reAuthStagesDataSource.finishReAuthEventLiveData
}