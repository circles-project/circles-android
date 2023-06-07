package org.futo.circles.di.ui

//val timelineUiModule = module {
//    viewModel { (roomId: String, type: CircleRoomTypeArg, threadEventId: String?) ->
//        TimelineViewModel(
//            get { parametersOf(roomId, type) },
//            get { parametersOf(roomId, type, threadEventId) },
//            get { parametersOf(roomId) },
//            get { parametersOf(roomId) },
//            get(), get(), get(), get()
//        )
//    }
//viewModel { (roomId: String) -> InviteMembersViewModel(get { parametersOf(roomId) }) }
//viewModel {
//    (roomId: String, type: CircleRoomTypeArg) ->
//    ManageMembersViewModel(get { parametersOf(roomId, type) })
//}
//viewModel {
//    (roomId: String, eventId: String) ->
//    ReportViewModel(get { parametersOf(roomId, eventId) })
//}
//viewModel { (roomId: String) -> FollowingViewModel(get { parametersOf(roomId) }) }
//viewModel { BaseShareViewModel(get()) }
//viewModel {
//    (levelValue: Int, myUserLevelValue: Int) ->
//    ChangeAccessLevelViewModel(get { parametersOf(levelValue, myUserLevelValue) })
//}
//}