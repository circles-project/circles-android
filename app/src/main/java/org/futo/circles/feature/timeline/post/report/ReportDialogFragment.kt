package org.futo.circles.feature.timeline.post.report

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.databinding.DialogFragmentReportBinding
import org.futo.circles.feature.timeline.post.report.list.ReportCategoryAdapter

@AndroidEntryPoint
class ReportDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentReportBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val args: ReportDialogFragmentArgs by navArgs()
    private val viewModel by viewModels<ReportViewModel>()
    private val listAdapter by lazy { ReportCategoryAdapter(::onReportCategorySelected) }

    private val binding by lazy {
        getBinding() as DialogFragmentReportBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvCategories.adapter = listAdapter
        binding.btnReport.setOnClickListener {
            viewModel.report(binding.scoreSlider.value.toInt())
            startLoading(binding.btnReport)
        }
        binding.tvSeverity.text = context?.getString(R.string.severity_formatter, 0)
        binding.scoreSlider.addOnChangeListener { _, value, _ ->
            binding.tvSeverity.text = context?.getString(R.string.severity_formatter, value.toInt())
        }
    }

    private fun setupObservers() {
        viewModel.reportLiveData.observeResponse(this) {
            showSuccess(getString(R.string.report_sent), true)
            onBackPressed()
        }
        viewModel.reportCategoriesLiveData.observeData(this) {
            listAdapter.submitList(it)
        }
    }

    private fun onReportCategorySelected(categoryId: Int) {
        viewModel.toggleReportCategory(categoryId)
        binding.btnReport.isEnabled = true
    }

}