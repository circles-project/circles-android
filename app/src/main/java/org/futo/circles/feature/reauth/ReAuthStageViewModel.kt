package org.futo.circles.feature.reauth

import org.futo.circles.core.auth.BaseLoginStagesViewModel

class ReAuthStageViewModel(reAuthStagesDataSource: ReAuthStagesDataSource) :
    BaseLoginStagesViewModel(reAuthStagesDataSource) {

    val finishReAuthEventLiveData = reAuthStagesDataSource.finishReAuthEventLiveData
}