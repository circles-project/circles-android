package com.futo.circles.di

import com.futo.circles.core.matrix.pass_phrase.create.CreatePassPhraseDataSource
import com.futo.circles.core.matrix.pass_phrase.restore.RestorePassPhraseDataSource
import com.futo.circles.core.matrix.room.CoreSpacesTreeBuilder
import com.futo.circles.core.matrix.room.CreateRoomDataSource
import com.futo.circles.core.matrix.room.RoomRelationsBuilder
import com.futo.circles.feature.circles.accept_invite.data_source.AcceptCircleInviteDataSource
import com.futo.circles.feature.circles.data_source.CirclesDataSource
import com.futo.circles.feature.circles.following.data_source.FollowingDataSource
import com.futo.circles.feature.groups.data_source.GroupsDataSource
import com.futo.circles.feature.home.data_source.HomeDataSource
import com.futo.circles.feature.log_in.data_source.LoginDataSource
import com.futo.circles.feature.room.invite.data_source.InviteMembersDataSource
import com.futo.circles.feature.room.manage_members.change_role.data_source.ChangeAccessLevelDataSource
import com.futo.circles.feature.room.manage_members.data_source.ManageMembersDataSource
import com.futo.circles.feature.room.select_users.data_source.SelectUsersDataSource
import com.futo.circles.feature.room.update_room.data_source.UpdateRoomDataSource
import com.futo.circles.feature.sign_up.data_source.SignUpDataSource
import com.futo.circles.feature.sign_up.setup_circles.data_source.SetupCirclesDataSource
import com.futo.circles.feature.sign_up.setup_profile.data_source.SetupProfileDataSource
import com.futo.circles.feature.sign_up.sign_up_type.data_source.SelectSignUpTypeDataSource
import com.futo.circles.feature.sign_up.terms.data_source.AcceptTermsDataSource
import com.futo.circles.feature.sign_up.validate_email.data_source.ValidateEmailDataSource
import com.futo.circles.feature.sign_up.validate_token.data_source.ValidateTokenDataSource
import com.futo.circles.feature.timeline.data_source.TimelineBuilder
import com.futo.circles.feature.timeline.data_source.TimelineDataSource
import com.futo.circles.feature.timeline.post.emoji.data_source.EmojiDataSource
import com.futo.circles.feature.timeline.post.report.data_source.ReportDataSource
import com.futo.circles.model.CircleRoomTypeArg
import org.koin.dsl.module

val dataSourceModule = module {
    factory { LoginDataSource(get(), get()) }
    factory { (roomId: String, type: CircleRoomTypeArg) ->
        TimelineDataSource(roomId, type, get(), get(), get())
    }
    factory { TimelineBuilder() }
    factory { (roomId: String) -> InviteMembersDataSource(roomId, get()) }
    factory { (roomId: String?) -> SelectUsersDataSource(roomId) }
    factory { (roomId: String, type: CircleRoomTypeArg) ->
        ManageMembersDataSource(roomId, type, get())
    }
    factory { CreateRoomDataSource(get(), get()) }
    factory { RoomRelationsBuilder() }
    factory { CoreSpacesTreeBuilder(get()) }
    single { SignUpDataSource(get(), get(), get()) }
    factory { ValidateTokenDataSource(get()) }
    factory { SelectSignUpTypeDataSource(get(), get()) }
    factory { AcceptTermsDataSource(get(), get()) }
    factory { ValidateEmailDataSource(get()) }
    factory { SetupProfileDataSource(get()) }
    factory { SetupCirclesDataSource(get()) }
    factory { HomeDataSource(get()) }
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
    factory { (roomId: String) -> AcceptCircleInviteDataSource(roomId, get()) }
}