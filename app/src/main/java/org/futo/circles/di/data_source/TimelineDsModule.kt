package org.futo.circles.di.data_source

import org.futo.circles.feature.circles.following.FollowingDataSource
import org.futo.circles.feature.people.UserOptionsDataSource
import org.futo.circles.feature.people.user.UserDataSource
import org.futo.circles.feature.room.LeaveRoomDataSource
import org.futo.circles.feature.room.RoomNotificationsDataSource
import org.futo.circles.feature.room.invite.InviteMembersDataSource
import org.futo.circles.feature.room.manage_members.ManageMembersDataSource
import org.futo.circles.feature.room.manage_members.change_role.ChangeAccessLevelDataSource
import org.futo.circles.core.select_users.SearchUserDataSource
import org.futo.circles.core.select_users.SelectUsersDataSource
import org.futo.circles.feature.timeline.data_source.AccessLevelDataSource
import org.futo.circles.feature.timeline.data_source.ReadMessageDataSource
import org.futo.circles.feature.timeline.data_source.SendMessageDataSource
import org.futo.circles.feature.timeline.data_source.TimelineBuilder
import org.futo.circles.feature.timeline.data_source.TimelineDataSource
import org.futo.circles.feature.timeline.post.PostOptionsDataSource
import org.futo.circles.feature.timeline.post.report.ReportDataSource
import org.koin.dsl.module

val timelineDsModule = module {
    factory { (roomId: String, type: CircleRoomTypeArg, threadEventId: String?) ->
        TimelineDataSource(roomId, type, threadEventId, get())
    }
    factory { (roomId: String, type: CircleRoomTypeArg) ->
        RoomNotificationsDataSource(roomId, type, get())
    }
    factory { SendMessageDataSource(get()) }
    factory { (roomId: String) -> LeaveRoomDataSource(roomId, get()) }
    factory { PostOptionsDataSource(get()) }
    factory { TimelineBuilder() }
    factory { (roomId: String) -> AccessLevelDataSource(roomId) }
    factory { (roomId: String) -> InviteMembersDataSource(roomId, get()) }
    factory { (roomId: String?) -> SelectUsersDataSource(roomId, get()) }
    factory { SearchUserDataSource() }
    factory { (roomId: String, type: CircleRoomTypeArg) ->
        ManageMembersDataSource(roomId, type, get())
    }
    factory { (levelValue: Int, myUserLevelValue: Int) ->
        ChangeAccessLevelDataSource(levelValue, myUserLevelValue)
    }
    factory { (roomId: String, eventId: String) -> ReportDataSource(roomId, eventId, get()) }
    factory { (roomId: String) -> FollowingDataSource(roomId, get(), get()) }
    factory { UserOptionsDataSource() }
    factory { (userId: String) -> UserDataSource(get(), userId) }
    factory { ReadMessageDataSource() }
}