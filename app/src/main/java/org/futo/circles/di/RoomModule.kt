package org.futo.circles.di

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.room.leave.LeaveRoomDataSource

@Module
@InstallIn(ViewModelComponent::class)
abstract class RoomModule {

    @Provides
    @ViewModelScoped
    fun provideLeaveRoomDataSource(
        savedStateHandle: SavedStateHandle,
        leaveRoomDataSourceFactory: LeaveRoomDataSource.Factory
    ): LeaveRoomDataSource = leaveRoomDataSourceFactory.create(
        savedStateHandle.getOrThrow("roomId")
    )

}