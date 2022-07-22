package org.futo.circles.di

import org.futo.circles.core.matrix.auth.AuthConfirmationProvider
import org.futo.circles.core.matrix.pass_phrase.create.CreatePassPhraseDataSource
import org.futo.circles.core.matrix.pass_phrase.restore.RestorePassPhraseDataSource
import org.futo.circles.core.matrix.room.CoreSpacesTreeBuilder
import org.futo.circles.core.matrix.room.CreateRoomDataSource
import org.futo.circles.core.matrix.room.RoomRelationsBuilder
import org.futo.circles.core.picker.device.PickDeviceMediaDataSource
import org.futo.circles.feature.circles.CirclesDataSource
import org.futo.circles.feature.circles.accept_invite.AcceptCircleInviteDataSource
import org.futo.circles.feature.circles.following.FollowingDataSource
import org.futo.circles.feature.groups.GroupsDataSource
import org.futo.circles.feature.log_in.LoginDataSource
import org.futo.circles.feature.people.PeopleDataSource
import org.futo.circles.feature.people.UserOptionsDataSource
import org.futo.circles.feature.people.user.UserDataSource
import org.futo.circles.feature.photos.PhotosDataSource
import org.futo.circles.feature.photos.preview.MediaPreviewDataSource
import org.futo.circles.feature.photos.save.SelectGalleryDataSource
import org.futo.circles.feature.room.LeaveRoomDataSource
import org.futo.circles.feature.room.invite.InviteMembersDataSource
import org.futo.circles.feature.room.manage_members.ManageMembersDataSource
import org.futo.circles.feature.room.manage_members.change_role.ChangeAccessLevelDataSource
import org.futo.circles.feature.room.select_users.SelectUsersDataSource
import org.futo.circles.feature.room.update_room.UpdateRoomDataSource
import org.futo.circles.feature.settings.SettingsDataSource
import org.futo.circles.feature.settings.active_sessions.ActiveSessionsDataSource
import org.futo.circles.feature.settings.change_password.ChangePasswordDataSource
import org.futo.circles.feature.sign_up.SignUpDataSource
import org.futo.circles.feature.sign_up.setup_circles.SetupCirclesDataSource
import org.futo.circles.feature.sign_up.setup_profile.SetupProfileDataSource
import org.futo.circles.feature.sign_up.sign_up_type.SelectSignUpTypeDataSource
import org.futo.circles.feature.sign_up.terms.AcceptTermsDataSource
import org.futo.circles.feature.sign_up.validate_email.ValidateEmailDataSource
import org.futo.circles.feature.sign_up.validate_token.ValidateTokenDataSource
import org.futo.circles.feature.timeline.data_source.SendMessageDataSource
import org.futo.circles.feature.timeline.data_source.TimelineBuilder
import org.futo.circles.feature.timeline.data_source.TimelineDataSource
import org.futo.circles.feature.timeline.post.PostOptionsDataSource
import org.futo.circles.feature.timeline.post.emoji.EmojiDataSource
import org.futo.circles.feature.timeline.post.report.ReportDataSource
import org.futo.circles.model.CircleRoomTypeArg
import org.koin.dsl.module

val dataSourceModule = module {
    factory { LoginDataSource(get(), get(), get()) }
    factory { (roomId: String, type: CircleRoomTypeArg) ->
        TimelineDataSource(roomId, type, get())
    }
    factory { SendMessageDataSource(get()) }
    factory { (roomId: String) ->
        LeaveRoomDataSource(roomId, get())
    }
    factory { PostOptionsDataSource(get()) }
    factory { TimelineBuilder() }
    factory { (roomId: String) -> InviteMembersDataSource(roomId, get()) }
    factory { (roomId: String?) -> SelectUsersDataSource(roomId) }
    factory { (roomId: String, type: CircleRoomTypeArg) ->
        ManageMembersDataSource(roomId, type, get())
    }
    factory { CreateRoomDataSource(get(), get()) }
    factory { RoomRelationsBuilder() }
    factory { CoreSpacesTreeBuilder(get(), get()) }
    single { SignUpDataSource(get(), get(), get()) }
    factory { ValidateTokenDataSource(get()) }
    factory { SelectSignUpTypeDataSource(get(), get()) }
    factory { AcceptTermsDataSource(get(), get()) }
    factory { ValidateEmailDataSource(get()) }
    factory { SetupProfileDataSource(get()) }
    factory { SetupCirclesDataSource(get()) }
    factory { SettingsDataSource(get(), get()) }
    factory { CreatePassPhraseDataSource(get()) }
    factory { RestorePassPhraseDataSource(get()) }
    factory { (levelValue: Int, myUserLevelValue: Int) ->
        ChangeAccessLevelDataSource(levelValue, myUserLevelValue)
    }
    factory { (roomId: String) -> UpdateRoomDataSource(roomId, get()) }
    factory { (roomId: String, eventId: String) -> ReportDataSource(roomId, eventId, get()) }
    single { EmojiDataSource(get()) }
    factory { (roomId: String) -> FollowingDataSource(roomId, get(), get()) }
    factory { GroupsDataSource(get()) }
    factory { CirclesDataSource() }
    factory { PhotosDataSource() }
    factory { (roomId: String) -> AcceptCircleInviteDataSource(roomId, get()) }
    factory { ChangePasswordDataSource() }
    factory { ActiveSessionsDataSource(get(), get()) }
    factory { AuthConfirmationProvider() }
    factory { (roomId: String, eventId: String) -> MediaPreviewDataSource(roomId, eventId) }
    factory { SelectGalleryDataSource(get(), get()) }
    factory { PeopleDataSource() }
    factory { UserOptionsDataSource() }
    factory { (userId: String) -> UserDataSource(get(), userId) }
    single { PickDeviceMediaDataSource(get()) }
}