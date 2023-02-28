package org.futo.circles.di.data_source

import org.futo.circles.core.matrix.room.CoreSpacesTreeBuilder
import org.futo.circles.core.matrix.room.CreateRoomDataSource
import org.futo.circles.core.matrix.room.RoomRelationsBuilder
import org.futo.circles.feature.circles.CirclesDataSource
import org.futo.circles.feature.circles.accept_invite.AcceptCircleInviteDataSource
import org.futo.circles.feature.groups.GroupsDataSource
import org.futo.circles.feature.people.PeopleDataSource
import org.futo.circles.feature.photos.PhotosDataSource
import org.futo.circles.feature.room.select.SelectRoomsDataSource
import org.futo.circles.feature.room.update.UpdateRoomDataSource
import org.futo.circles.model.CircleRoomTypeArg
import org.koin.dsl.module

val roomDSModule = module {
    factory { CreateRoomDataSource(get(), get()) }
    factory { RoomRelationsBuilder() }
    factory { CoreSpacesTreeBuilder(get(), get()) }
    factory { (roomId: String) -> UpdateRoomDataSource(roomId, get(), get()) }
    factory { GroupsDataSource(get()) }
    factory { CirclesDataSource() }
    factory { PhotosDataSource() }
    factory { (roomId: String) -> AcceptCircleInviteDataSource(roomId, get()) }
    factory { PeopleDataSource(get()) }
    factory { (roomType: CircleRoomTypeArg) -> SelectRoomsDataSource(roomType) }
}