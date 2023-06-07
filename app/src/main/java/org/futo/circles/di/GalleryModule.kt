package org.futo.circles.di

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.gallery.feature.preview.MediaPreviewDataSource

@Module
@InstallIn(ViewModelComponent::class)
abstract class GalleryModule {

    @Provides
    @ViewModelScoped
    fun provideMediaPreviewDataSource(
        savedStateHandle: SavedStateHandle,
        mediaPreviewDataSourceFactory: MediaPreviewDataSource.Factory
    ): MediaPreviewDataSource = mediaPreviewDataSourceFactory.create(
        savedStateHandle.getOrThrow("roomId"),
        savedStateHandle.getOrThrow("eventId")
    )

}