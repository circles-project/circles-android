package com.futo.circles.feature.photos.gallery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.extensions.getCurrentUserPowerLevel
import com.futo.circles.extensions.getTimelineRoomFor
import com.futo.circles.feature.timeline.TimelineFragmentArgs
import com.futo.circles.feature.timeline.TimelineViewModel
import com.futo.circles.feature.timeline.list.TimelineAdapter
import com.futo.circles.feature.timeline.post.CreatePostListener
import com.futo.circles.feature.timeline.post.emoji.EmojiPickerListener
import com.futo.circles.model.CircleRoomTypeArg
import com.futo.circles.view.PostOptionsListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GalleryFragment : Fragment(R.layout.gallery_fragment) {

    private val args: GalleryFragmentArgs by navArgs()
    private val viewModel by viewModel<GalleryViewModel> { parametersOf(args.roomId) }


    private val binding by viewBinding(com.futo.circles.databinding.GalleryFragmentBinding::bind)
    private val listAdapter by lazy {
        TimelineAdapter(getCurrentUserPowerLevel(args.roomId), this) { viewModel.loadMore() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupViews()
        setupObservers()
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
        if (isGroupMode) inflateGroupMenu(menu, inflater) else inflateCircleMenu(menu, inflater)
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun inflateGroupMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.group_timeline_menu, menu)
        menu.findItem(R.id.configureGroup).isVisible = isGroupSettingAvailable
        menu.findItem(R.id.inviteMembers).isVisible = isGroupInviteAvailable
    }

    private fun inflateCircleMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.circle_timeline_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.configureGroup, R.id.configureCircle -> {
                navigateToUpdateRoom()
                return true
            }
            R.id.manageMembers, R.id.myFollowers -> {
                navigateToManageMembers()
                return true
            }
            R.id.inviteMembers, R.id.inviteFollowers -> {
                navigateToInviteMembers()
                return true
            }
            R.id.leaveGroup -> {
                showLeaveGroupDialog()
                return true
            }
            R.id.iFollowing -> {
                navigateToFollowing()
                return true
            }
            R.id.deleteCircle -> {
                showDeleteConfirmation()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
