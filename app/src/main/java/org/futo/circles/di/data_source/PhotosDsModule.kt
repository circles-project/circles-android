package org.futo.circles.di.data_source

import org.futo.circles.feature.photos.PhotosDataSource
import org.futo.circles.feature.photos.preview.MediaPreviewDataSource
import org.futo.circles.feature.photos.save.SavePostToGalleryDataSource
import org.futo.circles.feature.photos.select.SelectGalleriesDataSource
import org.koin.dsl.module

val photosDSModule = module {
    factory { PhotosDataSource() }
    factory { (roomId: String, eventId: String) -> MediaPreviewDataSource(roomId, eventId) }
    factory { SelectGalleriesDataSource() }
    factory { SavePostToGalleryDataSource(get(), get()) }
}