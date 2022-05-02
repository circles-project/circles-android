package com.futo.circles.feature.circle_timeline

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.databinding.TimelineFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class CircleTimelineFragment : Fragment(R.layout.timeline_fragment) {

    private val args: CircleTimelineFragmentArgs by navArgs()
    private val viewModel by viewModel<CircleTimelineViewModel> { parametersOf(args.roomId) }
    private val binding by viewBinding(TimelineFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
        inflater.inflate(R.menu.circle_timeline_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.configureCircle -> {

                return true
            }
            R.id.myFollowers -> {

                return true
            }
            R.id.iFollowing -> {

                return true
            }
            R.id.inviteFollowers -> {
                navigateToInviteMembers()
                return true
            }
            R.id.deleteCircle -> {

                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToInviteMembers() {
        findNavController().navigate(
            CircleTimelineFragmentDirections.toInviteMembersDialogFragment(args.roomId)
        )
    }
}