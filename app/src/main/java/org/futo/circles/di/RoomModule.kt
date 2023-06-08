package org.futo.circles.di

import androidx.lifecycle.SavedStateHandle
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.extensions.getOrThrow
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.room.leave.LeaveRoomDataSource
import org.futo.circles.core.room.update.UpdateRoomDataSource
import org.futo.circles.feature.circles.accept_invite.AcceptCircleInviteDataSource
import org.futo.circles.feature.circles.following.FollowingDataSource
import org.futo.circles.feature.people.user.UserDataSource
import org.futo.circles.feature.room.RoomNotificationsDataSource
import org.futo.circles.feature.room.invite.InviteMembersDataSource
import org.futo.circles.feature.room.manage_members.ManageMembersDataSource
import org.futo.circles.feature.room.manage_members.change_role.ChangeAccessLevelDataSource
import org.futo.circles.feature.room.select.SelectRoomsDataSource
import org.futo.circles.feature.room.select.SelectRoomsFragment
import org.futo.circles.feature.timeline.data_source.AccessLevelDataSource
import org.futo.circles.feature.timeline.post.report.ReportDataSource

@Module
@InstallIn(ViewModelComponent::class)
abstract class RoomModule {

    @Provides
    @ViewModelScoped
    fun provideLeaveRoomDataSource(
        savedStateHandle: SavedStateHandle,
        factory: LeaveRoomDataSource.Factory
    ): LeaveRoomDataSource = factory.create(savedStateHandle.getOrThrow("roomId"))

    @Provides
    @ViewModelScoped
    fun provideUpdateRoomDataSource(
        savedStateHandle: SavedStateHandle,
        factory: UpdateRoomDataSource.Factory
    ): UpdateRoomDataSource = factory.create(savedStateHandle.getOrThrow("roomId"))

    @Provides
    @ViewModelScoped
    fun provideAcceptCircleInviteDataSource(
        savedStateHandle: SavedStateHandle,
        factory: AcceptCircleInviteDataSource.Factory
    ): AcceptCircleInviteDataSource = factory.create(savedStateHandle.getOrThrow("roomId"))

    @Provides
    @ViewModelScoped
    fun provideSelectRoomsDataSource(
        savedStateHandle: SavedStateHandle,
        factory: SelectRoomsDataSource.Factory
    ): SelectRoomsDataSource {
        val ordinal = savedStateHandle.getOrThrow<Int>(SelectRoomsFragment.TYPE_ORDINAL)
        return factory.create(
            CircleRoomTypeArg.values().firstOrNull { it.ordinal == ordinal }
                ?: CircleRoomTypeArg.Circle
        )
    }

    @Provides
    @ViewModelScoped
    fun provideUserDataSource(
        savedStateHandle: SavedStateHandle,
        factory: UserDataSource.Factory
    ): UserDataSource = factory.create(savedStateHandle.getOrThrow("userId"))

    @Provides
    @ViewModelScoped
    fun provideChangeAccessLevelDataSource(
        savedStateHandle: SavedStateHandle,
        factory: ChangeAccessLevelDataSource.Factory
    ): ChangeAccessLevelDataSource = factory.create(
        savedStateHandle.getOrThrow("levelValue"),
        savedStateHandle.getOrThrow("myUserLevelValue"),
    )

    @Provides
    @ViewModelScoped
    fun provideFollowingSource(
        savedStateHandle: SavedStateHandle,
        factory: FollowingDataSource.Factory
    ): FollowingDataSource = factory.create(savedStateHandle.getOrThrow("roomId"))


    @Provides
    @ViewModelScoped
    fun provideReportDataSource(
        savedStateHandle: SavedStateHandle,
        factory: ReportDataSource.Factory
    ): ReportDataSource = factory.create(
        savedStateHandle.getOrThrow("roomId"),
        savedStateHandle.getOrThrow("eventId"),
    )

    @Provides
    @ViewModelScoped
    fun provideManageMemberDataSource(
        savedStateHandle: SavedStateHandle,
        factory: ManageMembersDataSource.Factory
    ): ManageMembersDataSource = factory.create(
        savedStateHandle.getOrThrow("roomId"),
        savedStateHandle.getOrThrow("type"),
    )

    @Provides
    @ViewModelScoped
    fun provideInviteMemberDataSource(
        savedStateHandle: SavedStateHandle,
        factory: InviteMembersDataSource.Factory
    ): InviteMembersDataSource = factory.create(savedStateHandle.getOrThrow("roomId"))


    @Provides
    @ViewModelScoped
    fun provideRoomNotificationsDataSource(
        savedStateHandle: SavedStateHandle,
        factory: RoomNotificationsDataSource.Factory
    ): RoomNotificationsDataSource = factory.create(
        savedStateHandle.getOrThrow("roomId"),
        savedStateHandle.getOrThrow("type"),
    )

    @Provides
    @ViewModelScoped
    fun provideAccessLevelDataSource(
        savedStateHandle: SavedStateHandle,
        factory: AccessLevelDataSource.Factory
    ): AccessLevelDataSource = factory.create(savedStateHandle.getOrThrow("roomId"))
}