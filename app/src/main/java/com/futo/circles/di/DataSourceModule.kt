package com.futo.circles.di

import com.futo.circles.core.matrix.pass_phrase.create.CreatePassPhraseDataSource
import com.futo.circles.core.matrix.pass_phrase.restore.RestorePassPhraseDataSource
import com.futo.circles.core.matrix.room.CoreSpacesTreeBuilder
import com.futo.circles.core.matrix.room.CreateRoomDataSource
import com.futo.circles.feature.configure_group.data_source.ConfigureGroupDataSource
import com.futo.circles.feature.group_invite.data_source.InviteMembersDataSource
import com.futo.circles.feature.group_members.change_role.data_source.ChangeAccessLevelDataSource
import com.futo.circles.feature.group_members.data_source.ManageGroupMembersDataSource
import com.futo.circles.feature.group_timeline.data_source.GroupTimelineBuilder
import com.futo.circles.feature.group_timeline.data_source.GroupTimelineDatasource
import com.futo.circles.feature.home.data_source.HomeDataSource
import com.futo.circles.feature.log_in.data_source.LoginDataSource
import com.futo.circles.feature.report.data_source.ReportDataSource
import com.futo.circles.feature.select_users.data_source.SelectUsersDataSource
import com.futo.circles.feature.setup_circles.data_source.SetupCirclesDataSource
import com.futo.circles.feature.setup_profile.data_source.SetupProfileDataSource
import com.futo.circles.feature.sign_up.data_source.SignUpDataSource
import com.futo.circles.feature.sign_up_type.data_source.SelectSignUpTypeDataSource
import com.futo.circles.feature.terms.data_source.AcceptTermsDataSource
import com.futo.circles.feature.validate_email.data_source.ValidateEmailDataSource
import com.futo.circles.feature.validate_token.data_source.ValidateTokenDataSource
import org.koin.dsl.module

val dataSourceModule = module {
    factory { LoginDataSource(get(), get()) }
    factory { (roomId: String) -> GroupTimelineDatasource(roomId, get(), get()) }
    factory { GroupTimelineBuilder() }
    factory { (roomId: String) -> InviteMembersDataSource(roomId, get()) }
    factory { (roomId: String?) -> SelectUsersDataSource(roomId) }
    factory { (roomId: String) -> ManageGroupMembersDataSource(roomId, get()) }
    factory { CreateRoomDataSource(get()) }
    factory { CoreSpacesTreeBuilder(get()) }
    single { SignUpDataSource(get(), get(), get()) }
    factory { ValidateTokenDataSource(get()) }
    factory { SelectSignUpTypeDataSource(get(), get()) }
    factory { AcceptTermsDataSource(get(), get()) }
    factory { ValidateEmailDataSource(get()) }
    factory { SetupProfileDataSource(get()) }
    factory { SetupCirclesDataSource(get()) }
    factory { HomeDataSource() }
    factory { CreatePassPhraseDataSource(get()) }
    factory { RestorePassPhraseDataSource(get()) }
    factory { (levelValue: Int, myUserLevelValue: Int) ->
        ChangeAccessLevelDataSource(levelValue, myUserLevelValue)
    }
    factory { (roomId: String) -> ConfigureGroupDataSource(roomId, get()) }
    factory { (roomId: String, eventId: String) -> ReportDataSource(roomId, eventId, get()) }
}