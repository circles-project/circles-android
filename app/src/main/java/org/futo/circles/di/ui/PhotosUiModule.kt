package org.futo.circles.di.ui

import org.futo.circles.feature.photos.PhotosViewModel
import org.futo.circles.feature.photos.backup.MediaBackupViewModel
import org.futo.circles.feature.photos.gallery.GalleryViewModel
import org.futo.circles.feature.photos.preview.MediaPreviewViewModel
import org.futo.circles.feature.photos.save.SavePostToGalleryViewModel
import org.futo.circles.feature.photos.select.SelectGalleriesViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val photosUiModule = module {
    viewModel { PhotosViewModel(get()) }
    viewModel { (roomId: String, type: CircleRoomTypeArg, isVideoAvailable: Boolean) ->
        GalleryViewModel(
            roomId,
            isVideoAvailable,
            get { parametersOf(roomId, type, null) },
            get { parametersOf(roomId) },
            get()
        )
    }
    viewModel { SelectGalleriesViewModel(get()) }
    viewModel { MediaBackupViewModel(get(), get()) }
    viewModel { (roomId: String, eventId: String) ->
        MediaPreviewViewModel(roomId, eventId, get { parametersOf(roomId, eventId) }, get())
    }
    viewModel { (roomId: String, eventId: String) ->
        SavePostToGalleryViewModel(get { parametersOf(roomId, eventId) }, get())
    }
}