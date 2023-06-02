package org.futo.circles.di.data_source

import org.futo.circles.gallery.feature.PhotosDataSource
import org.futo.circles.gallery.feature.backup.MediaBackupDataSource
import org.futo.circles.gallery.feature.preview.MediaPreviewDataSource
import org.futo.circles.gallery.feature.save.SavePostToGalleryDataSource
import org.futo.circles.gallery.feature.select.SelectGalleriesDataSource
import org.koin.dsl.module

val photosDSModule = module {
    factory { PhotosDataSource() }
    factory { (roomId: String, eventId: String) -> MediaPreviewDataSource(roomId, eventId) }
    factory { SelectGalleriesDataSource() }
    factory { SavePostToGalleryDataSource(get(), get()) }
    factory { MediaBackupDataSource(get(), get(), get(), get()) }
}