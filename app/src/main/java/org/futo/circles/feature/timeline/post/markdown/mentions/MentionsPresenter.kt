package org.futo.circles.feature.timeline.post.markdown.mentions

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.otaliastudios.autocomplete.RecyclerViewPresenter
import org.futo.circles.mapping.toUserListItem
import org.futo.circles.model.UserListItem
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.query.QueryStringValue
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.getUserOrDefault
import org.matrix.android.sdk.api.session.room.members.roomMemberQueryParams
import org.matrix.android.sdk.api.session.room.model.Membership

class MentionsPresenter(
    context: Context,
    private val roomId: String
) : RecyclerViewPresenter<UserListItem>(context) {

    private val adapter = MentionsAdapter { dispatchClick(it) }

    override fun onQuery(query: CharSequence?) {
        val session = MatrixSessionProvider.currentSession ?: return
        val users = session.getRoom(roomId)?.membershipService()?.getRoomMembers(
            roomMemberQueryParams {
                displayName = if (query.isNullOrBlank()) QueryStringValue.IsNotEmpty
                else QueryStringValue.Contains(
                    query.toString(),
                    QueryStringValue.Case.INSENSITIVE
                )
                memberships = listOf(Membership.JOIN)
                excludeSelf = true
            }
        )?.map {
            session.getUserOrDefault(it.userId).toUserListItem(false)
        }
        adapter.submitList(users)
    }

    override fun instantiateAdapter(): RecyclerView.Adapter<*> = adapter
}