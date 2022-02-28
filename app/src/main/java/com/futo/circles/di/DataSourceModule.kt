package com.futo.circles.di

import com.futo.circles.ui.groups.timeline.data_source.GroupTimelineBuilder
import com.futo.circles.ui.groups.timeline.data_source.GroupTimelineDatasource
import com.futo.circles.ui.groups.timeline.invite.data_source.InviteMembersDataSource
import com.futo.circles.ui.log_in.data_source.LoginDataSource
import org.koin.dsl.module

val dataSourceModule = module {
    factory { LoginDataSource(get()) }

    factory { (roomId: String) -> GroupTimelineDatasource(roomId, get()) }

    factory { GroupTimelineBuilder() }

    factory { (roomId: String) -> InviteMembersDataSource(roomId, get()) }
}