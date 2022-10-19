package org.futo.circles.feature.reauth

import org.futo.circles.R
import org.futo.circles.core.auth.BaseLoginStagesFragment
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.onBackPressed
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReAuthStagesFragment : BaseLoginStagesFragment() {
    override val viewModel by viewModel<ReAuthStageViewModel>()
    override val isReAuth: Boolean = true
    override val titleResId = R.string.confirm_auth

    override fun setupObservers() {
        super.setupObservers()
        viewModel.finishReAuthEventLiveData.observeData(this) {
            onBackPressed()
        }
    }
}