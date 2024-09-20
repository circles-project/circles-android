package org.futo.circles.auth.feature.uia.stages.recaptcha

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import javax.inject.Inject


@HiltViewModel
class RecaptchaViewModel @Inject constructor(
    private val dataSource: RecaptchaDataSource
) : ViewModel() {

    val recaptchaResultLiveData = SingleEventLiveData<Response<Unit>>()
    val recaptchaParamsLiveData = MutableLiveData(dataSource.getRecaptchaParams())


    fun handleRecaptcha(response: String) {
        launchBg {
            recaptchaResultLiveData.postValue(dataSource.handleRecaptcha(response))
        }
    }

}