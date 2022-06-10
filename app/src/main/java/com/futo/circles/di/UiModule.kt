package com.futo.circles.di

import com.futo.circles.feature.circles.CirclesViewModel
import com.futo.circles.feature.circles.accept_invite.AcceptCircleInviteViewModel
import com.futo.circles.feature.circles.following.FollowingViewModel
import com.futo.circles.feature.groups.GroupsViewModel
import com.futo.circles.feature.log_in.LogInViewModel
import com.futo.circles.feature.people.PeopleViewModel
import com.futo.circles.feature.photos.PhotosViewModel
import com.futo.circles.feature.photos.gallery.GalleryViewModel
import com.futo.circles.feature.photos.preview.GalleryImageViewModel
import com.futo.circles.feature.photos.save.SaveToGalleryViewModel
import com.futo.circles.feature.room.create_room.CreateRoomViewModel
import com.futo.circles.feature.room.invite.InviteMembersViewModel
import com.futo.circles.feature.room.manage_members.ManageMembersViewModel
import com.futo.circles.feature.room.manage_members.change_role.ChangeAccessLevelViewModel
import com.futo.circles.feature.room.select_users.SelectUsersViewModel
import com.futo.circles.feature.room.update_room.UpdateRoomViewModel
import com.futo.circles.feature.settings.SettingsViewModel
import com.futo.circles.feature.settings.active_sessions.ActiveSessionsViewModel
import com.futo.circles.feature.settings.active_sessions.remove_session.RemoveSessionViewModel
import com.futo.circles.feature.settings.change_password.ChangePasswordViewModel
import com.futo.circles.feature.settings.deactivate.DeactivateAccountViewModel
import com.futo.circles.feature.settings.edit_profile.EditProfileViewModel
import com.futo.circles.feature.sign_up.SignUpViewModel
import com.futo.circles.feature.sign_up.setup_circles.SetupCirclesViewModel
import com.futo.circles.feature.sign_up.setup_profile.SetupProfileViewModel
import com.futo.circles.feature.sign_up.sign_up_type.SelectSignUpTypeViewModel
import com.futo.circles.feature.sign_up.terms.AcceptTermsViewModel
import com.futo.circles.feature.sign_up.validate_email.ValidateEmailViewModel
import com.futo.circles.feature.sign_up.validate_token.ValidateTokenViewModel
import com.futo.circles.feature.timeline.TimelineViewModel
import com.futo.circles.feature.timeline.post.emoji.EmojiViewModel
import com.futo.circles.feature.timeline.post.report.ReportViewModel
import com.futo.circles.model.CircleRoomTypeArg
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val uiModule = module {
    viewModel { LogInViewModel(get()) }
    viewModel { GroupsViewModel(get()) }
    viewModel { CirclesViewModel(get()) }
    viewModel { PeopleViewModel(get(), get()) }
    viewModel { PhotosViewModel(get()) }
    viewModel { (roomId: String, type: CircleRoomTypeArg) ->
        TimelineViewModel(
            get { parametersOf(roomId, type) }, get { parametersOf(roomId) }, get(), get(), get()
        )
    }
    viewModel { (roomId: String) -> InviteMembersViewModel(get { parametersOf(roomId) }) }
    viewModel { (roomId: String, type: CircleRoomTypeArg) ->
        ManageMembersViewModel(get { parametersOf(roomId, type) })
    }
    viewModel { (roomId: String?) -> SelectUsersViewModel(get { parametersOf(roomId) }) }
    viewModel { CreateRoomViewModel(get()) }
    viewModel { SignUpViewModel(get()) }
    viewModel { ValidateTokenViewModel(get()) }
    viewModel { SelectSignUpTypeViewModel(get()) }
    viewModel { AcceptTermsViewModel(get()) }
    viewModel { ValidateEmailViewModel(get()) }
    viewModel { SetupProfileViewModel(get()) }
    viewModel { SetupCirclesViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { (levelValue: Int, myUserLevelValue: Int) ->
        ChangeAccessLevelViewModel(get { parametersOf(levelValue, myUserLevelValue) })
    }
    viewModel { (roomId: String) -> UpdateRoomViewModel(get { parametersOf(roomId) }) }
    viewModel { (roomId: String, eventId: String) ->
        ReportViewModel(get { parametersOf(roomId, eventId) })
    }
    viewModel { EmojiViewModel(get()) }
    viewModel { (roomId: String) -> FollowingViewModel(get { parametersOf(roomId) }) }
    viewModel { (roomId: String) -> AcceptCircleInviteViewModel(get { parametersOf(roomId) }) }
    viewModel { EditProfileViewModel(get()) }
    viewModel { ChangePasswordViewModel(get(), get()) }
    viewModel { DeactivateAccountViewModel(get()) }
    viewModel { ActiveSessionsViewModel(get()) }
    viewModel { (deviceId: String) -> RemoveSessionViewModel(get { parametersOf(deviceId) }) }
    viewModel { (roomId: String, type: CircleRoomTypeArg) ->
        GalleryViewModel(
            roomId, get { parametersOf(roomId, type) }, get { parametersOf(roomId) }, get()
        )
    }
    viewModel { (roomId: String, eventId: String) ->
        GalleryImageViewModel(roomId, eventId, get { parametersOf(roomId, eventId) }, get())
    }
    viewModel { (roomId: String, eventId: String) ->
        SaveToGalleryViewModel(get { parametersOf(roomId, eventId) }, get())
    }
}