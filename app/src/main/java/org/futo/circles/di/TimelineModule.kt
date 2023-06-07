package org.futo.circles.di

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.select_users.SelectUsersDataSource
import org.futo.circles.core.timeline.TimelineDataSource

@Module
@InstallIn(ViewModelComponent::class)
abstract class TimelineModule {

    @Provides
    @ViewModelScoped
    fun provideTimelineDataSource(
        savedStateHandle: SavedStateHandle,
        timelineDataSourceFactory: TimelineDataSource.Factory
    ): TimelineDataSource = timelineDataSourceFactory.create(
        savedStateHandle.getOrThrow("roomId"),
        savedStateHandle.getOrThrow("type"),
        savedStateHandle["threadEventId"]
    )

    @Provides
    @ViewModelScoped
    fun provideSelectUserDataSource(
        savedStateHandle: SavedStateHandle,
        selectUserDataSourceFactory: SelectUsersDataSource.Factory
    ): SelectUsersDataSource = selectUserDataSourceFactory.create(
        savedStateHandle.getOrThrow("roomId")
    )

}