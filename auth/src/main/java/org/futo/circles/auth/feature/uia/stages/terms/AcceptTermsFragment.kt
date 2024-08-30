package org.futo.circles.auth.feature.uia.stages.terms

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.databinding.FragmentAcceptTermsBinding
import org.futo.circles.auth.feature.uia.stages.terms.list.TermsListAdapter
import org.futo.circles.auth.model.TermsListItem
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.base.fragment.ParentBackPressOwnerFragment
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.openCustomTabUrl

@AndroidEntryPoint
class AcceptTermsFragment :
    ParentBackPressOwnerFragment<FragmentAcceptTermsBinding>(FragmentAcceptTermsBinding::inflate),
    HasLoadingState {

    private val viewModel by viewModels<AcceptTermsViewModel>()
    override val fragment: Fragment = this
    private val listAdapter by lazy {
        TermsListAdapter(onViewTerms = { item -> openTermsUrl(item) })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            ivBack.setOnClickListener { onBackPressed() }
            btnAcceptAll.setOnClickListener {
                startLoading(btnAcceptAll)
                viewModel.acceptTerms()
            }
            btnRejectAll.setOnClickListener { onBackPressed() }
            rvTerms.adapter = listAdapter
        }
    }

    private fun setupObservers() {
        viewModel.acceptTermsLiveData.observeResponse(this)

        viewModel.termsListLiveData.observeData(this) {
            listAdapter.submitList(it)
        }
    }

    private fun openTermsUrl(item: TermsListItem) {
        openCustomTabUrl(item.url)
    }
}