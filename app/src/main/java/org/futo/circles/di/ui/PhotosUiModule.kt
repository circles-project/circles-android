package org.futo.circles.di.ui

//val photosUiModule = module {
// viewModel { PhotosViewModel(get()) }
//    viewModel { (roomId: String, type: CircleRoomTypeArg, isVideoAvailable: Boolean) ->
//        GalleryViewModel(
//            roomId,
//            isVideoAvailable,
//            get { parametersOf(roomId, type, null) },
//            get { parametersOf(roomId) },
//            get()
//        )
//    }
//viewModel { SelectGalleriesViewModel(get()) }
//viewModel { MediaBackupViewModel(get(), get()) }
//    viewModel { (roomId: String, eventId: String) ->
//        MediaPreviewViewModel(roomId, eventId, get { parametersOf(roomId, eventId) }, get())
//    }
//    viewModel { (roomId: String, eventId: String) ->
//        SavePostToGalleryViewModel(get { parametersOf(roomId, eventId) }, get())
//    }
//}