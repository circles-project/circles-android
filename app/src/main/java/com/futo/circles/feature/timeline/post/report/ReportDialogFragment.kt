package com.futo.circles.feature.timeline.post.report

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.futo.circles.R
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.core.fragment.HasLoadingState
import com.futo.circles.databinding.ReportDialogFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.observeResponse
import com.futo.circles.extensions.showSuccess
import com.futo.circles.feature.timeline.post.report.list.ReportCategoryAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class ReportDialogFragment :
    BaseFullscreenDialogFragment(ReportDialogFragmentBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val args: ReportDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<ReportViewModel> { parametersOf(args.roomId, args.eventId) }
    private val listAdapter by lazy { ReportCategoryAdapter(::onReportCategorySelected) }

    private val binding by lazy {
        getBinding() as ReportDialogFragmentBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
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
            activity?.onBackPressed()
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