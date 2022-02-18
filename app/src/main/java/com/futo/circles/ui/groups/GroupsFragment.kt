package com.futo.circles.ui.groups

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.databinding.GroupsFragmentBinding
import com.futo.circles.extensions.observeData
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.matrix.android.sdk.api.session.group.model.GroupSummary

class GroupsFragment : Fragment(R.layout.groups_fragment) {

    private val viewModel by viewModel<GroupsViewModel>()
    private val binding by viewBinding(GroupsFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.groupsLiveData?.observeData(this, ::setGroupsList)
    }

    private fun setGroupsList(list: List<GroupSummary>) {

    }
}