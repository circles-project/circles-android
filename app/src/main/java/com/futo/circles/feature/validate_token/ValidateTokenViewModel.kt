package com.futo.circles.feature.validate_token

import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.validate_token.data_source.ValidateTokenDataSource

class ValidateTokenViewModel(
    private val dataSource: ValidateTokenDataSource
) : ViewModel() {

    val validateLiveData = SingleEventLiveData<Response<okhttp3.Response>>()

    fun validateToken(token: String) {
        launchBg { dataSource.validateToken(token) }
    }

}