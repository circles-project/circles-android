package org.futo.circles.core.model

import androidx.annotation.StringRes
import org.futo.circles.core.R
import org.matrix.android.sdk.api.session.room.model.RoomJoinRules
import org.matrix.android.sdk.api.session.room.model.RoomType

private const val orgPrefix = "org.futo"

const val GROUP_TYPE = "$orgPrefix.social.group"
const val GALLERY_TYPE = "$orgPrefix.social.gallery"
const val TIMELINE_TYPE = "$orgPrefix.social.timeline"

const val ROOT_SPACE_ACCOUNT_DATA_KEY = "root"
const val TIMELINES_SPACE_ACCOUNT_DATA_KEY = "timelines"
const val PROFILE_SPACE_ACCOUNT_DATA_KEY = "profile"
const val PHOTOS_SPACE_ACCOUNT_DATA_KEY = "galleries"
const val PEOPLE_SPACE_ACCOUNT_DATA_KEY = "people"
const val GROUPS_SPACE_ACCOUNT_DATA_KEY = "groups"


sealed class CirclesRoom(
    @StringRes open val nameId: Int?,
    open val parentAccountDataKey: String?,
    open val type: String?,
    open val joinRules: RoomJoinRules?,
    open val accountDataKey: String?
) {
    fun isSpace(): Boolean = type == RoomType.SPACE

    open fun getTag(): String? = null
}

data class RootSpace(
    override val nameId: Int? = R.string.root_space_name,
    override val parentAccountDataKey: String? = null,
    override val type: String? = RoomType.SPACE,
    override val joinRules: RoomJoinRules? = null,
    override val accountDataKey: String? = ROOT_SPACE_ACCOUNT_DATA_KEY
) : CirclesRoom(nameId, parentAccountDataKey, type, joinRules, accountDataKey) {
    override fun getTag(): String = "$orgPrefix.space.root"
}

data class TimelinesSpace(
    override val nameId: Int? = R.string.timelines_space_name,
    override val parentAccountDataKey: String? = ROOT_SPACE_ACCOUNT_DATA_KEY,
    override val type: String? = RoomType.SPACE,
    override val joinRules: RoomJoinRules? = null,
    override val accountDataKey: String? = TIMELINES_SPACE_ACCOUNT_DATA_KEY
) : CirclesRoom(nameId, parentAccountDataKey, type, joinRules, accountDataKey) {
    override fun getTag(): String = "$orgPrefix.space.timelines"
}

data class SharedCirclesSpace(
    override val nameId: Int? = R.string.shared_circles,
    override val parentAccountDataKey: String? = ROOT_SPACE_ACCOUNT_DATA_KEY,
    override val type: String? = RoomType.SPACE,
    override val joinRules: RoomJoinRules? = RoomJoinRules.KNOCK,
    override val accountDataKey: String? = PROFILE_SPACE_ACCOUNT_DATA_KEY
) : CirclesRoom(nameId, parentAccountDataKey, type, joinRules, accountDataKey) {
    override fun getTag(): String = "$orgPrefix.space.circles.shared"
}

data class PhotosSpace(
    override val nameId: Int? = R.string.photos_space_name,
    override val parentAccountDataKey: String? = ROOT_SPACE_ACCOUNT_DATA_KEY,
    override val type: String? = RoomType.SPACE,
    override val joinRules: RoomJoinRules? = null,
    override val accountDataKey: String? = PHOTOS_SPACE_ACCOUNT_DATA_KEY
) : CirclesRoom(nameId, parentAccountDataKey, type, joinRules, accountDataKey) {
    override fun getTag(): String = "$orgPrefix.space.photos"
}

data class PeopleSpace(
    override val nameId: Int? = R.string.people_space_name,
    override val parentAccountDataKey: String? = ROOT_SPACE_ACCOUNT_DATA_KEY,
    override val type: String? = RoomType.SPACE,
    override val joinRules: RoomJoinRules? = null,
    override val accountDataKey: String? = PEOPLE_SPACE_ACCOUNT_DATA_KEY
) : CirclesRoom(nameId, parentAccountDataKey, type, joinRules, accountDataKey) {

    override fun getTag(): String = "$orgPrefix.space.people"
}

data class GroupsSpace(
    override val nameId: Int? = R.string.groups_space_name,
    override val parentAccountDataKey: String? = ROOT_SPACE_ACCOUNT_DATA_KEY,
    override val type: String? = RoomType.SPACE,
    override val joinRules: RoomJoinRules? = null,
    override val accountDataKey: String? = GROUPS_SPACE_ACCOUNT_DATA_KEY
) : CirclesRoom(nameId, parentAccountDataKey, type, joinRules, accountDataKey) {

    override fun getTag(): String = "$orgPrefix.space.groups"
}


data class Group(
    override val nameId: Int? = null,
    override val parentAccountDataKey: String? = GROUPS_SPACE_ACCOUNT_DATA_KEY,
    override val type: String? = GROUP_TYPE,
    override val joinRules: RoomJoinRules? = RoomJoinRules.KNOCK
) : CirclesRoom(nameId, parentAccountDataKey, type, joinRules, null)

data class Gallery(
    override val nameId: Int? = null,
    override val parentAccountDataKey: String? = PHOTOS_SPACE_ACCOUNT_DATA_KEY,
    override val type: String? = GALLERY_TYPE,
    override val joinRules: RoomJoinRules? = RoomJoinRules.KNOCK
) : CirclesRoom(nameId, parentAccountDataKey, type, joinRules, null)

data class Timeline(
    override val nameId: Int? = null,
    override val parentAccountDataKey: String? = TIMELINES_SPACE_ACCOUNT_DATA_KEY,
    override val type: String? = TIMELINE_TYPE,
    override val joinRules: RoomJoinRules? = RoomJoinRules.KNOCK
) : CirclesRoom(nameId, parentAccountDataKey, type, joinRules, null)