package com.futo.circles.model

import androidx.annotation.StringRes
import com.futo.circles.R
import org.matrix.android.sdk.api.session.room.model.RoomType

private const val ROOT_SPACE_TAG = "m.space.root"
private const val CIRCLES_SPACE_TAG = "m.space.circles"
private const val GROUPS_SPACE_TAG = "m.space.groups"
private const val PHOTOS_SPACE_TAG = "m.space.photos"
const val CIRCLE_TAG = "m.social.circle"
const val GROUP_TAG = "m.social.group"
const val TIMELINE_TAG = "m.social.timeline"

const val GROUP_TYPE = GROUP_TAG
const val TIMELINE_TYPE = TIMELINE_TAG


sealed class CirclesRoom(
    @StringRes open val nameId: Int?,
    open val tag: String,
    open val parentTag: String?,
    open val type: String?
) {
    fun isSpace(): Boolean = type == RoomType.SPACE
}

data class RootSpace(
    override val nameId: Int? = R.string.root_space_name,
    override val tag: String = ROOT_SPACE_TAG,
    override val parentTag: String? = null,
    override val type: String? = RoomType.SPACE
) : CirclesRoom(nameId, tag, parentTag, type)

data class CirclesSpace(
    override val nameId: Int? = R.string.circles_space_name,
    override val tag: String = CIRCLES_SPACE_TAG,
    override val parentTag: String? = ROOT_SPACE_TAG,
    override val type: String? = RoomType.SPACE
) : CirclesRoom(nameId, tag, parentTag, type)

data class PhotosSpace(
    override val nameId: Int? = R.string.photos_space_name,
    override val tag: String = PHOTOS_SPACE_TAG,
    override val parentTag: String? = ROOT_SPACE_TAG,
    override val type: String? = RoomType.SPACE
) : CirclesRoom(nameId, tag, parentTag, type)

data class GroupsSpace(
    override val nameId: Int? = R.string.groups_space_name,
    override val tag: String = GROUPS_SPACE_TAG,
    override val parentTag: String? = ROOT_SPACE_TAG,
    override val type: String? = RoomType.SPACE
) : CirclesRoom(nameId, tag, parentTag, type)

data class Circle(
    override val nameId: Int? = null,
    override val tag: String = CIRCLE_TAG,
    override val parentTag: String? = CIRCLES_SPACE_TAG,
    override val type: String? = RoomType.SPACE
) : CirclesRoom(nameId, tag, parentTag, type)

data class Group(
    override val nameId: Int? = null,
    override val tag: String = GROUP_TAG,
    override val parentTag: String? = GROUPS_SPACE_TAG,
    override val type: String? = GROUP_TYPE
) : CirclesRoom(nameId, tag, parentTag, type)

data class Timeline(
    override val nameId: Int? = null,
    override val tag: String = TIMELINE_TAG,
    override val parentTag: String? = null,
    override val type: String? = TIMELINE_TYPE
) : CirclesRoom(nameId, tag, parentTag, type)