package org.futo.circles.di.ui

import org.futo.circles.feature.circles.CirclesViewModel
import org.futo.circles.feature.circles.accept_invite.AcceptCircleInviteViewModel
import org.futo.circles.feature.groups.GroupsViewModel
import org.futo.circles.feature.people.PeopleViewModel
import org.futo.circles.feature.photos.PhotosViewModel
import org.futo.circles.feature.photos.gallery.GalleryViewModel
import org.futo.circles.feature.photos.select.SelectGalleriesViewModel
import org.futo.circles.feature.room.create.CreateRoomViewModel
import org.futo.circles.feature.room.select.SelectRoomsViewModel
import org.futo.circles.feature.room.select_users.SelectUsersViewModel
import org.futo.circles.feature.room.update.UpdateRoomViewModel
import org.futo.circles.feature.timeline.state.RoomStateEventsViewModel
import org.futo.circles.model.CircleRoomTypeArg
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val roomUiModule = module {
    viewModel { GroupsViewModel(get()) }
    viewModel { CirclesViewModel(get()) }
    viewModel { PeopleViewModel(get()) }
    viewModel { PhotosViewModel(get()) }
    viewModel { (roomId: String?) -> SelectUsersViewModel(get { parametersOf(roomId) }) }
    viewModel { CreateRoomViewModel(get()) }
    viewModel { (roomId: String) -> UpdateRoomViewModel(get { parametersOf(roomId) }) }
    viewModel { (roomId: String) -> AcceptCircleInviteViewModel(get { parametersOf(roomId) }) }
    viewModel { (roomId: String, type: CircleRoomTypeArg, isVideoAvailable: Boolean) ->
        GalleryViewModel(
            roomId,
            isVideoAvailable,
            get { parametersOf(roomId, type) },
            get { parametersOf(roomId) },
            get()
        )
    }
    viewModel { SelectGalleriesViewModel(get()) }
    viewModel { (type: CircleRoomTypeArg) -> SelectRoomsViewModel(get { parametersOf(type) }) }
    viewModel { (roomId: String) -> RoomStateEventsViewModel(roomId) }
}