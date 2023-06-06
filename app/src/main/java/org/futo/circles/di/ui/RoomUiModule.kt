package org.futo.circles.di.ui

import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.feature.circles.CirclesViewModel
import org.futo.circles.feature.circles.accept_invite.AcceptCircleInviteViewModel
import org.futo.circles.feature.groups.GroupsViewModel
import org.futo.circles.feature.people.PeopleViewModel
import org.futo.circles.core.room.create.CreateRoomViewModel
import org.futo.circles.feature.room.select.SelectRoomsViewModel
import org.futo.circles.core.select_users.SelectUsersViewModel
import org.futo.circles.core.room.update.UpdateRoomViewModel
import org.futo.circles.feature.timeline.state.RoomStateEventsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val roomUiModule = module {
    viewModel { GroupsViewModel(get()) }
//    viewModel { CirclesViewModel(get()) }
    viewModel { PeopleViewModel(get(), get()) }
    viewModel { (roomId: String?) -> SelectUsersViewModel(get { parametersOf(roomId) }) }
    viewModel { CreateRoomViewModel(get()) }
    viewModel { (roomId: String) -> UpdateRoomViewModel(get { parametersOf(roomId) }) }
    viewModel { (roomId: String) -> AcceptCircleInviteViewModel(get { parametersOf(roomId) }) }
    viewModel { (type: CircleRoomTypeArg) -> SelectRoomsViewModel(get { parametersOf(type) }) }
    viewModel { (roomId: String) -> RoomStateEventsViewModel(roomId) }
}