package com.futo.circles.di

import com.futo.circles.core.matrix.auth.AuthConfirmationProvider
import com.futo.circles.core.matrix.pass_phrase.create.CreatePassPhraseDataSource
import com.futo.circles.core.matrix.pass_phrase.restore.RestorePassPhraseDataSource
import com.futo.circles.core.matrix.room.CoreSpacesTreeBuilder
import com.futo.circles.core.matrix.room.CreateRoomDataSource
import com.futo.circles.core.matrix.room.RoomRelationsBuilder
import com.futo.circles.feature.circles.CirclesDataSource
import com.futo.circles.feature.circles.accept_invite.AcceptCircleInviteDataSource
import com.futo.circles.feature.circles.following.FollowingDataSource
import com.futo.circles.feature.groups.GroupsDataSource
import com.futo.circles.feature.log_in.LoginDataSource
import com.futo.circles.feature.photos.PhotosDataSource
import com.futo.circles.feature.photos.preview.GalleryImageDataSource
import com.futo.circles.feature.photos.save.SelectGalleryDataSource
import com.futo.circles.feature.room.LeaveRoomDataSource
import com.futo.circles.feature.room.invite.InviteMembersDataSource
import com.futo.circles.feature.room.manage_members.ManageMembersDataSource
import com.futo.circles.feature.room.manage_members.change_role.ChangeAccessLevelDataSource
import com.futo.circles.feature.room.select_users.SelectUsersDataSource
import com.futo.circles.feature.room.update_room.UpdateRoomDataSource
import com.futo.circles.feature.settings.SettingsDataSource
import com.futo.circles.feature.settings.active_sessions.ActiveSessionsDataSource
import com.futo.circles.feature.settings.active_sessions.remove_session.RemoveSessionDataSource
import com.futo.circles.feature.settings.change_password.ChangePasswordDataSource
import com.futo.circles.feature.settings.deactivate.DeactivateAccountDataSource
import com.futo.circles.feature.sign_up.SignUpDataSource
import com.futo.circles.feature.sign_up.setup_circles.SetupCirclesDataSource
import com.futo.circles.feature.sign_up.setup_profile.SetupProfileDataSource
import com.futo.circles.feature.sign_up.sign_up_type.SelectSignUpTypeDataSource
import com.futo.circles.feature.sign_up.terms.AcceptTermsDataSource
import com.futo.circles.feature.sign_up.validate_email.ValidateEmailDataSource
import com.futo.circles.feature.sign_up.validate_token.ValidateTokenDataSource
import com.futo.circles.feature.timeline.data_source.SendMessageDataSource
import com.futo.circles.feature.timeline.data_source.TimelineBuilder
import com.futo.circles.feature.timeline.data_source.TimelineDataSource
import com.futo.circles.feature.timeline.post.PostOptionsDataSource
import com.futo.circles.feature.timeline.post.emoji.EmojiDataSource
import com.futo.circles.feature.timeline.post.report.ReportDataSource
import com.futo.circles.model.CircleRoomTypeArg
import org.koin.dsl.module

val dataSourceModule = module {
    factory { LoginDataSource(get(), get()) }
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
    factory { SettingsDataSource(get()) }
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
    factory { DeactivateAccountDataSource(get(), get()) }
    factory { ActiveSessionsDataSource(get()) }
    factory { AuthConfirmationProvider() }
    factory { (deviceId: String) -> RemoveSessionDataSource(deviceId, get(), get()) }
    factory { (roomId: String, eventId: String) -> GalleryImageDataSource(roomId, eventId) }
    factory { SelectGalleryDataSource(get(), get()) }
}