package org.futo.circles.auth.feature.reauth

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ReAuthStageViewModel @Inject constructor(reAuthStagesDataSource: ReAuthStagesDataSource) : ViewModel() {

    val subtitleLiveData = reAuthStagesDataSource.subtitleLiveData
    val loginStageNavigationLiveData = reAuthStagesDataSource.loginStageNavigationLiveData
    val finishReAuthEventLiveData = reAuthStagesDataSource.finishReAuthEventLiveData
}