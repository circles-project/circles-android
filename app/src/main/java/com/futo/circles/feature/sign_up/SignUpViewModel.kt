package com.futo.circles.feature.sign_up

import androidx.lifecycle.ViewModel

class SignUpViewModel(
    datasource: SignUpDataSource
) : ViewModel() {

    val subtitleLiveData = datasource.subtitleLiveData
    val passPhraseLoadingLiveData = datasource.passPhraseLoadingLiveData
    val finishRegistrationLiveData = datasource.finishRegistrationLiveData
    val navigationLiveData = datasource.navigationLiveData

}