package org.futo.circles.di

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.room.leave.LeaveRoomDataSource
import org.futo.circles.core.room.update.UpdateRoomDataSource
import org.futo.circles.feature.circles.accept_invite.AcceptCircleInviteDataSource
import org.futo.circles.feature.room.select.SelectRoomsDataSource
import org.futo.circles.feature.room.select.SelectRoomsFragment

@Module
@InstallIn(ViewModelComponent::class)
abstract class RoomModule {

    @Provides
    @ViewModelScoped
    fun provideLeaveRoomDataSource(
        savedStateHandle: SavedStateHandle,
        factory: LeaveRoomDataSource.Factory
    ): LeaveRoomDataSource = factory.create(savedStateHandle.getOrThrow("roomId"))

    @Provides
    @ViewModelScoped
    fun provideUpdateRoomDataSource(
        savedStateHandle: SavedStateHandle,
        factory: UpdateRoomDataSource.Factory
    ): UpdateRoomDataSource = factory.create(savedStateHandle.getOrThrow("roomId"))

    @Provides
    @ViewModelScoped
    fun provideAcceptCircleInviteDataSource(
        savedStateHandle: SavedStateHandle,
        factory: AcceptCircleInviteDataSource.Factory
    ): AcceptCircleInviteDataSource = factory.create(savedStateHandle.getOrThrow("roomId"))

    @Provides
    @ViewModelScoped
    fun provideSelectRoomsDataSource(
        savedStateHandle: SavedStateHandle,
        factory: SelectRoomsDataSource.Factory
    ): SelectRoomsDataSource {
        val ordinal = savedStateHandle.getOrThrow<Int>(SelectRoomsFragment.TYPE_ORDINAL)
        return factory.create(
            CircleRoomTypeArg.values().firstOrNull { it.ordinal == ordinal }
                ?: CircleRoomTypeArg.Circle
        )
    }

}