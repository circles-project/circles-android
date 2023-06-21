package org.futo.circles.feature.room.well_known

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.extensions.getOrThrow
import javax.inject.Inject

@HiltViewModel
class RoomWellKnownViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val roomId: String = savedStateHandle.getOrThrow("roomId")

}