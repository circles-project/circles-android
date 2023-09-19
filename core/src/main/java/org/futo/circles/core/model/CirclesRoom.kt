package org.futo.circles.core.model

import androidx.annotation.StringRes
import org.futo.circles.core.R
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules
import org.matrix.android.sdk.api.session.room.model.RoomType

private const val orgPrefix = "org.futo"
private const val CIRCLES_SPACE_TAG = "$orgPrefix.space.circles"
const val SHARED_CIRCLES_SPACE_TAG = "$orgPrefix.space.circles.shared"
private const val GROUPS_SPACE_TAG = "$orgPrefix.space.groups"
const val PHOTOS_SPACE_TAG = "$orgPrefix.space.photos"
const val PEOPLE_SPACE_TAG = "$orgPrefix.space.people"
const val ROOT_SPACE_TAG = "$orgPrefix.space.root"
const val CIRCLE_TAG = "$orgPrefix.social.circle"

const val GROUP_TYPE = "$orgPrefix.social.group"
const val GALLERY_TYPE = "$orgPrefix.social.gallery"
const val TIMELINE_TYPE = "$orgPrefix.social.timeline"


sealed class CirclesRoom(
    @StringRes open val nameId: Int?,
    open val tag: String?,
    open val parentTag: String?,
    open val type: String?,
    open val joinRules: RoomJoinRules?
) {
    fun isSpace(): Boolean = type == RoomType.SPACE
}

data class RootSpace(
    override val nameId: Int? = R.string.root_space_name,
    override val tag: String? = ROOT_SPACE_TAG,
    override val parentTag: String? = null,
    override val type: String? = RoomType.SPACE,
    override val joinRules: RoomJoinRules? = null
) : CirclesRoom(nameId, tag, parentTag, type, joinRules)

data class CirclesSpace(
    override val nameId: Int? = R.string.circles_space_name,
    override val tag: String? = CIRCLES_SPACE_TAG,
    override val parentTag: String? = ROOT_SPACE_TAG,
    override val type: String? = RoomType.SPACE,
    override val joinRules: RoomJoinRules? = null
) : CirclesRoom(nameId, tag, parentTag, type, joinRules)

data class SharedCirclesSpace(
    override val nameId: Int? = R.string.shared_circles,
    override val tag: String? = SHARED_CIRCLES_SPACE_TAG,
    override val parentTag: String? = CIRCLES_SPACE_TAG,
    override val type: String? = RoomType.SPACE,
    override val joinRules: RoomJoinRules? = RoomJoinRules.KNOCK
) : CirclesRoom(nameId, tag, parentTag, type, joinRules)

data class PhotosSpace(
    override val nameId: Int? = R.string.photos_space_name,
    override val tag: String? = PHOTOS_SPACE_TAG,
    override val parentTag: String? = ROOT_SPACE_TAG,
    override val type: String? = RoomType.SPACE,
    override val joinRules: RoomJoinRules? = null
) : CirclesRoom(nameId, tag, parentTag, type, joinRules)

data class PeopleSpace(
    override val nameId: Int? = R.string.peopel_space_name,
    override val tag: String? = PEOPLE_SPACE_TAG,
    override val parentTag: String? = ROOT_SPACE_TAG,
    override val type: String? = RoomType.SPACE,
    override val joinRules: RoomJoinRules? = null
) : CirclesRoom(nameId, tag, parentTag, type, joinRules)

data class GroupsSpace(
    override val nameId: Int? = R.string.groups_space_name,
    override val tag: String? = GROUPS_SPACE_TAG,
    override val parentTag: String? = ROOT_SPACE_TAG,
    override val type: String? = RoomType.SPACE,
    override val joinRules: RoomJoinRules? = null
) : CirclesRoom(nameId, tag, parentTag, type, joinRules)

data class Circle(
    override val nameId: Int? = null,
    override val tag: String? = CIRCLE_TAG,
    override val parentTag: String? = CIRCLES_SPACE_TAG,
    override val type: String? = RoomType.SPACE,
    override val joinRules: RoomJoinRules? = null
) : CirclesRoom(nameId, tag, parentTag, type, joinRules)

data class Group(
    override val nameId: Int? = null,
    override val tag: String? = null,
    override val parentTag: String? = GROUPS_SPACE_TAG,
    override val type: String? = GROUP_TYPE,
    override val joinRules: RoomJoinRules? = RoomJoinRules.KNOCK
) : CirclesRoom(nameId, tag, parentTag, type, joinRules)

data class Gallery(
    override val nameId: Int? = null,
    override val tag: String? = null,
    override val parentTag: String? = PHOTOS_SPACE_TAG,
    override val type: String? = GALLERY_TYPE,
    override val joinRules: RoomJoinRules? = RoomJoinRules.INVITE
) : CirclesRoom(nameId, tag, parentTag, type, joinRules)

data class Timeline(
    override val nameId: Int? = null,
    override val tag: String? = null,
    override val parentTag: String? = null,
    override val type: String? = TIMELINE_TYPE,
    override val joinRules: RoomJoinRules? = RoomJoinRules.KNOCK
) : CirclesRoom(nameId, tag, parentTag, type, joinRules)