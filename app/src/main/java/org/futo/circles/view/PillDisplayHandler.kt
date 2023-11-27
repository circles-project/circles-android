/*
 * Copyright (c) 2023 New Vector Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.futo.circles.view

import android.text.style.ReplacementSpan
import io.element.android.wysiwyg.display.KeywordDisplayHandler
import io.element.android.wysiwyg.display.LinkDisplayHandler
import io.element.android.wysiwyg.display.TextDisplay
import org.matrix.android.sdk.api.session.permalinks.PermalinkData
import org.matrix.android.sdk.api.session.permalinks.PermalinkParser
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.util.MatrixItem
import org.matrix.android.sdk.api.util.toEveryoneInRoomMatrixItem
import org.matrix.android.sdk.api.util.toMatrixItem
import org.matrix.android.sdk.api.util.toRoomAliasMatrixItem

/**
 * A rich text editor [LinkDisplayHandler] and [KeywordDisplayHandler]
 * that helps with replacing user and room links with pills.
 */
internal class PillDisplayHandler(
        private val roomId: String,
        private val getRoom: (roomId: String) -> RoomSummary?,
        private val getMember: (userId: String) -> RoomMemberSummary?,
        private val replacementSpanFactory: (matrixItem: MatrixItem) -> ReplacementSpan,
) : LinkDisplayHandler, KeywordDisplayHandler {
    override fun resolveLinkDisplay(text: String, url: String): TextDisplay {
        val matrixItem = when (val permalink = PermalinkParser.parse(url)) {
            is PermalinkData.UserLink -> {
                val userId = permalink.userId
                when (val roomMember = getMember(userId)) {
                    null -> MatrixItem.UserItem(userId, userId, null)
                    else -> roomMember.toMatrixItem()
                }
            }
            is PermalinkData.RoomLink -> {
                val roomId = permalink.roomIdOrAlias
                val room = getRoom(roomId)
                when {
                    room == null -> MatrixItem.RoomItem(roomId, roomId, null)
                    text == MatrixItem.NOTIFY_EVERYONE -> room.toEveryoneInRoomMatrixItem()
                    permalink.isRoomAlias -> room.toRoomAliasMatrixItem()
                    else -> room.toMatrixItem()
                }
            }
            else ->
                return TextDisplay.Plain
        }
        val replacement = replacementSpanFactory.invoke(matrixItem)
        return TextDisplay.Custom(customSpan = replacement)
    }

    override val keywords: List<String>
        get() = listOf(MatrixItem.NOTIFY_EVERYONE)

    override fun resolveKeywordDisplay(text: String): TextDisplay =
            when (text) {
                MatrixItem.NOTIFY_EVERYONE -> {
                    val matrixItem = getRoom(roomId)?.toEveryoneInRoomMatrixItem()
                            ?: MatrixItem.EveryoneInRoomItem(roomId)
                    TextDisplay.Custom(replacementSpanFactory.invoke(matrixItem))
                }
                else -> TextDisplay.Plain
            }
}
