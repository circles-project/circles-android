package org.futo.circles.feature.sign_up.terms

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
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
import org.koin.core.parameter.parametersOf

class AcceptTermsFragment : ParentBackPressOwnerFragment(R.layout.fragment_accept_terms),
    HasLoadingState {

    private val args: AcceptTermsFragmentArgs by navArgs()
    private val viewModel by viewModel<AcceptTermsViewModel> {
        parametersOf(args.isLoginMode)
    }
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
            binding.btnAccept.isEnabled = isAllTermsAccepted(it)
        }
    }

    private fun isAllTermsAccepted(list: List<TermsListItem>): Boolean {
        list.forEach { if (!it.isChecked) return false }
        return true
    }

    private fun openTermsUrl(item: TermsListItem) {
        openCustomTabUrl(item.url)
    }
}