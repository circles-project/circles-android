package org.futo.circles.di.data_source

//val timelineDsModule = module {
//    factory { (roomId: String, type: CircleRoomTypeArg, threadEventId: String?) ->
//        TimelineDataSource(roomId, type, threadEventId, get())
//    }
//    factory { (roomId: String, type: CircleRoomTypeArg) ->
//        RoomNotificationsDataSource(roomId, type, get())
//    }
//factory { SendMessageDataSource(get()) }
//factory { (roomId: String) -> LeaveRoomDataSource(roomId, get()) }
//factory { PostOptionsDataSource(get()) }
//factory { TimelineBuilder() }
//factory { (roomId: String) -> AccessLevelDataSource(roomId) }
//factory { (roomId: String) -> InviteMembersDataSource(roomId, get()) }
//factory { (roomId: String?) -> SelectUsersDataSource(roomId, get()) }
//factory { SearchUserDataSource() }
//    factory { (roomId: String, type: CircleRoomTypeArg) ->
//        ManageMembersDataSource(roomId, type, get())
//    }
//    factory { (levelValue: Int, myUserLevelValue: Int) ->
//        ChangeAccessLevelDataSource(levelValue, myUserLevelValue)
//    }
//factory { (roomId: String, eventId: String) -> ReportDataSource(roomId, eventId, get()) }
//factory { (roomId: String) -> FollowingDataSource(roomId, get(), get()) }
//factory { UserOptionsDataSource() }
//factory { (userId: String) -> UserDataSource(get(), userId) }
//factory { ReadMessageDataSource() }
//}