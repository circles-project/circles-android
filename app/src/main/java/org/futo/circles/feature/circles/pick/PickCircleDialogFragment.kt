package org.futo.circles.feature.circles.pick

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.feature.room.select.SelectRoomsFragment
import org.futo.circles.core.feature.room.select.interfaces.SelectRoomsListener
import org.futo.circles.core.model.SelectRoomTypeArg
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.databinding.DialogFragmentPickCircleBinding
import org.futo.circles.model.PickCircleTypeArg

@AndroidEntryPoint
class PickCircleDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentPickCircleBinding>(DialogFragmentPickCircleBinding::inflate),
    SelectRoomsListener {

    private val args: PickCircleDialogFragmentArgs by navArgs()

    private val selectRoomsFragment by lazy {
        val selectRoomType = when (args.type) {
            PickCircleTypeArg.AllPostsSettings -> SelectRoomTypeArg.CirclesJoined
            PickCircleTypeArg.CreatePost -> SelectRoomTypeArg.MyCircles
        }
        SelectRoomsFragment.create(selectRoomType, isMultiSelect = false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (savedInstanceState == null) addSelectCirclesFragment()
        setupViews()
    }

    private fun addSelectCirclesFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.lContainer, selectRoomsFragment)
            .commitAllowingStateLoss()
    }

    private fun setupViews() {
        binding.toolbar.title = getString(
            when (args.type) {
                PickCircleTypeArg.AllPostsSettings -> R.string.circles_settings
                PickCircleTypeArg.CreatePost -> R.string.choose_circle_to_post
            }
        )
    }

    override fun onRoomsSelected(rooms: List<SelectableRoomListItem>) {
        val roomId = rooms.firstOrNull()?.id ?: return
        when (args.type) {
            PickCircleTypeArg.AllPostsSettings -> findNavController().navigateSafe(
                PickCircleDialogFragmentDirections.toTimelineOptions(roomId)
            )

            PickCircleTypeArg.CreatePost -> findNavController().navigateSafe(
                PickCircleDialogFragmentDirections.toCreatePost(roomId)
            )
        }
    }
}