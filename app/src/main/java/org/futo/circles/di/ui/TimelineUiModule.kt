package org.futo.circles.di.ui

import org.futo.circles.feature.circles.following.FollowingViewModel
import org.futo.circles.feature.room.invite.InviteMembersViewModel
import org.futo.circles.feature.room.manage_members.ManageMembersViewModel
import org.futo.circles.feature.room.manage_members.change_role.ChangeAccessLevelViewModel
import org.futo.circles.feature.share.BaseShareViewModel
import org.futo.circles.feature.timeline.TimelineViewModel
import org.futo.circles.feature.timeline.post.report.ReportViewModel
import org.futo.circles.feature.timeline.thread.ThreadTimelineViewModel
import org.futo.circles.model.CircleRoomTypeArg
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val timelineUiModule = module {
    viewModel { (roomId: String, type: CircleRoomTypeArg) ->
        TimelineViewModel(
            get { parametersOf(roomId, type) },
            get { parametersOf(roomId, type) },
            get { parametersOf(roomId) },
            get { parametersOf(roomId) },
            get(), get(), get(), get()
        )
    }
    viewModel { (roomId: String) -> InviteMembersViewModel(get { parametersOf(roomId) }) }
    viewModel { (roomId: String, type: CircleRoomTypeArg) ->
        ManageMembersViewModel(get { parametersOf(roomId, type) })
    }
    viewModel { (roomId: String, eventId: String) ->
        ReportViewModel(get { parametersOf(roomId, eventId) })
    }
    viewModel { (roomId: String) -> FollowingViewModel(get { parametersOf(roomId) }) }
    viewModel { BaseShareViewModel(get()) }
    viewModel { (levelValue: Int, myUserLevelValue: Int) ->
        ChangeAccessLevelViewModel(get { parametersOf(levelValue, myUserLevelValue) })
    }
    viewModel { (roomId: String, eventId: String) ->
        ThreadTimelineViewModel(
            get { parametersOf(roomId) },
            get { parametersOf(roomId, eventId) },
            get(), get(), get()
        )
    }
}