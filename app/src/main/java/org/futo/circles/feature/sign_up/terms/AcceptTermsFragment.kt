package org.futo.circles.feature.sign_up.terms

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.core.fragment.ParentBackPressOwnerFragment
import org.futo.circles.databinding.FragmentAcceptTermsBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.openCustomTabUrl
import org.futo.circles.feature.sign_up.terms.list.TermsListAdapter
import org.futo.circles.model.TermsListItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class AcceptTermsFragment : ParentBackPressOwnerFragment(R.layout.fragment_accept_terms),
    HasLoadingState {

    override val fragment: Fragment = this
    private val binding by viewBinding(FragmentAcceptTermsBinding::bind)
    private val viewModel by viewModel<AcceptTermsViewModel>()
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
            binding.btnAccept.isEnabled = viewModel.isAllTermsAccepted(it)
        }
    }

    private fun openTermsUrl(item: TermsListItem) {
        openCustomTabUrl(item.url)
    }
}