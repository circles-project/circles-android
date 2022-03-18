package com.futo.circles.di

import com.futo.circles.feature.create_group.data_source.CreateGroupDataSource
import com.futo.circles.feature.group_invite.data_source.InviteMembersDataSource
import com.futo.circles.feature.group_timeline.data_source.GroupTimelineBuilder
import com.futo.circles.feature.group_timeline.data_source.GroupTimelineDatasource
import com.futo.circles.feature.log_in.data_source.LoginDataSource
import com.futo.circles.feature.manage_group_members.data_source.ManageGroupMembersDataSource
import com.futo.circles.feature.select_users.data_source.SelectUsersDataSource
import com.futo.circles.feature.sign_up.data_source.SignUpDataSource
import com.futo.circles.feature.sign_up_type.data_source.SelectSignUpTypeDataSource
import com.futo.circles.feature.terms.data_source.AcceptTermsDataSource
import com.futo.circles.feature.validate_token.data_source.ValidateTokenDataSource
import org.koin.dsl.module

val dataSourceModule = module {
    factory { LoginDataSource(get()) }

    factory { (roomId: String) -> GroupTimelineDatasource(roomId, get()) }

    factory { GroupTimelineBuilder() }

    factory { (roomId: String) -> InviteMembersDataSource(roomId, get()) }

    factory { (roomId: String?) -> SelectUsersDataSource(roomId) }

    factory { (roomId: String) -> ManageGroupMembersDataSource(roomId, get()) }

    factory { CreateGroupDataSource() }

    single { SignUpDataSource(get()) }

    factory { ValidateTokenDataSource(get()) }

    factory { SelectSignUpTypeDataSource(get()) }

    factory { AcceptTermsDataSource(get()) }
}