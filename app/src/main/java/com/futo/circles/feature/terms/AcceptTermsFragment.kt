package com.futo.circles.feature.terms

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.core.HasLoadingState
import com.futo.circles.databinding.AcceptTermsFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.observeResponse
import com.futo.circles.extensions.openCustomTabUrl
import com.futo.circles.feature.terms.list.TermsListAdapter
import com.futo.circles.model.TermsListItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class AcceptTermsFragment : Fragment(R.layout.accept_terms_fragment), HasLoadingState {

    override val fragment: Fragment = this
    private val binding by viewBinding(AcceptTermsFragmentBinding::bind)
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