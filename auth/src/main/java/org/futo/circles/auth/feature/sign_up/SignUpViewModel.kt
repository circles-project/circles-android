package org.futo.circles.auth.feature.sign_up

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    datasource: SignUpDataSource
) : ViewModel() {

    val subtitleLiveData = datasource.subtitleLiveData
    val passPhraseLoadingLiveData = datasource.passPhraseLoadingLiveData
    val finishRegistrationLiveData = datasource.finishRegistrationLiveData
    val navigationLiveData = datasource.navigationLiveData

}