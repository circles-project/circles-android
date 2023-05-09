package org.futo.circles.di.ui

import org.futo.circles.feature.home.SystemNoticesCountSharedViewModel
import org.futo.circles.feature.notices.SystemNoticesTimelineViewModel
import org.futo.circles.feature.people.user.UserViewModel
import org.futo.circles.feature.rageshake.BugReportViewModel
import org.futo.circles.feature.settings.SettingsViewModel
import org.futo.circles.feature.settings.active_sessions.ActiveSessionsViewModel
import org.futo.circles.feature.settings.active_sessions.verify.VerifySessionViewModel
import org.futo.circles.feature.settings.change_password.ChangePasswordViewModel
import org.futo.circles.feature.settings.profile.edit.EditProfileViewModel
import org.futo.circles.feature.settings.profile.share.ShareProfileViewModel
import org.futo.circles.feature.sign_up.username.UsernameViewModel
import org.futo.circles.model.CircleRoomTypeArg
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val settingsUiModule = module {
    viewModel { SettingsViewModel(get()) }
    viewModel { EditProfileViewModel(get()) }
    viewModel { ChangePasswordViewModel(get()) }
    viewModel { ActiveSessionsViewModel(get()) }
    viewModel { (userId: String) ->
        UserViewModel(
            userId,
            get { parametersOf(userId) },
            get(),
            get()
        )
    }
    viewModel { SystemNoticesCountSharedViewModel() }
    viewModel { (roomId: String, type: CircleRoomTypeArg) ->
        SystemNoticesTimelineViewModel(get { parametersOf(roomId, type, null) })
    }
    viewModel { UsernameViewModel(get()) }
    viewModel { (deviceId: String) -> VerifySessionViewModel(deviceId, get()) }
    viewModel { ShareProfileViewModel() }
    viewModel { BugReportViewModel(get(), get()) }
}