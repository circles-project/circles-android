package org.futo.circles.di.ui

import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.gallery.feature.PhotosViewModel
import org.futo.circles.gallery.feature.backup.MediaBackupViewModel
import org.futo.circles.gallery.feature.gallery.GalleryViewModel
import org.futo.circles.gallery.feature.preview.MediaPreviewViewModel
import org.futo.circles.gallery.feature.save.SavePostToGalleryViewModel
import org.futo.circles.gallery.feature.select.SelectGalleriesViewModel
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