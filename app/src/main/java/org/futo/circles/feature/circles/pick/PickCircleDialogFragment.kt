package org.futo.circles.feature.circles.pick

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
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

    private var pickCircleListener: PickCircleListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pickCircleListener = (parentFragment as? PickCircleListener)
    }

    private val selectRoomsFragment by lazy {
        val selectRoomType = when (args.type) {
            PickCircleTypeArg.AllPostsSettings -> SelectRoomTypeArg.CirclesJoined
            PickCircleTypeArg.CreatePost,
            PickCircleTypeArg.CreatePoll -> SelectRoomTypeArg.MyCircles
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
                PickCircleTypeArg.CreatePoll -> R.string.choose_circle_for_poll
            }
        )
    }

    override fun onRoomsSelected(rooms: List<SelectableRoomListItem>) {
        rooms.firstOrNull()?.let {
            pickCircleListener?.onCircleChosen(it.id, args.type)
            dismiss()
        }
    }
}