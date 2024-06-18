package org.futo.circles.core.feature.room.create

enum class CreateRoomProgressStage { CreateRoom, CreateTimeline, SetParentRelations, SetTimelineRelations, Finished }

interface CreateRoomProgressListener {

    fun onProgressUpdated(event: CreateRoomProgressStage)

}