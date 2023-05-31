package org.futo.circles.di.data_source

import org.futo.circles.core.room.CoreSpacesTreeBuilder
import org.futo.circles.core.room.CreateRoomDataSource
import org.futo.circles.core.room.RoomRelationsBuilder
import org.futo.circles.feature.circles.CirclesDataSource
import org.futo.circles.feature.circles.accept_invite.AcceptCircleInviteDataSource
import org.futo.circles.feature.groups.GroupsDataSource
import org.futo.circles.feature.people.PeopleDataSource
import org.futo.circles.feature.room.RoomAccountDataSource
import org.futo.circles.feature.room.select.SelectRoomsDataSource
import org.futo.circles.feature.room.update.UpdateRoomDataSource
import org.koin.dsl.module

val roomDSModule = module {
    factory { (roomId: String) -> UpdateRoomDataSource(roomId, get(), get()) }
    factory { GroupsDataSource(get()) }
    factory { CirclesDataSource() }
    factory { (roomId: String) -> AcceptCircleInviteDataSource(roomId, get()) }
    factory { PeopleDataSource(get()) }
    factory { (roomType: CircleRoomTypeArg) -> SelectRoomsDataSource(roomType) }
    factory { RoomAccountDataSource() }

    ///-----
    factory { CreateRoomDataSource(get(), get()) }
    factory { RoomRelationsBuilder() }
    factory { CoreSpacesTreeBuilder(get(), get()) }
}