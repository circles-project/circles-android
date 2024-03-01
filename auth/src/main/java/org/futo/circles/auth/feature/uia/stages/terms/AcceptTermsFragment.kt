package org.futo.circles.auth.feature.uia.stages.terms

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentAcceptTermsBinding
import org.futo.circles.auth.extensions.openCustomTabUrl
import org.futo.circles.auth.feature.uia.stages.terms.list.TermsListAdapter
import org.futo.circles.auth.model.TermsListItem
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.base.fragment.ParentBackPressOwnerFragment
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse

@AndroidEntryPoint
class AcceptTermsFragment : ParentBackPressOwnerFragment(R.layout.fragment_accept_terms),
    HasLoadingState {

    private val viewModel by viewModels<AcceptTermsViewModel>()
    override val fragment: Fragment = this
    private val binding by viewBinding(FragmentAcceptTermsBinding::bind)
    private val listAdapter by lazy {
        TermsListAdapter(
            onCheckChanged = { item -> viewModel.changeTermCheck(item) },
            onViewTerms = { item -> openTermsUrl(item) },
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.termsListLiveData.value?.let { setAcceptEnabled(it) }
    }

    private fun setupViews() {
        with(binding) {
            btnAccept.setOnClickListener {
                startLoading(btnAccept)
                viewModel.acceptTerms()
            }

            rvTerms.apply {
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                adapter = listAdapter
            }
        }
    }

    private fun setupObservers() {
        viewModel.acceptTermsLiveData.observeResponse(this)

        viewModel.termsListLiveData.observeData(this) {
            listAdapter.submitList(it)
            setAcceptEnabled(it)
        }
    }

    private fun setAcceptEnabled(list: List<TermsListItem>) {
        binding.btnAccept.isEnabled = isAllTermsAccepted(list)
    }

    private fun isAllTermsAccepted(list: List<TermsListItem>): Boolean {
        list.forEach { if (!it.isChecked) return false }
        return true
    }

    private fun openTermsUrl(item: TermsListItem) {
        openCustomTabUrl(item.url)
    }
}