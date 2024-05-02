package org.futo.circles.core.provider

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import org.matrix.android.sdk.api.Matrix
import org.matrix.android.sdk.api.MatrixConfiguration
import org.matrix.android.sdk.api.SyncConfig
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.statistics.StatisticEvent
import org.matrix.android.sdk.api.session.sync.filter.SyncFilterParams

object MatrixSessionProvider {

    var currentSession: Session? = null
        private set

    private var notificationSetupListener: MatrixNotificationSetupListener? = null
    private var onNewAuthLister: (() -> Unit)? = null

    fun getSessionOrThrow() =
        currentSession ?: throw IllegalArgumentException("Session is not created")

    fun initSession(
        context: Context,
        notificationListener: MatrixNotificationSetupListener? = null,
        newAuthListener: (() -> Unit)? = null
    ) {
        onNewAuthLister = newAuthListener
        notificationSetupListener = notificationListener
        Matrix(
            context = context, matrixConfiguration = MatrixConfiguration(
                roomDisplayNameFallbackProvider = RoomDisplayNameFallbackProviderImpl(context),
                syncConfig = SyncConfig(
                    syncFilterParams = SyncFilterParams(
                        lazyLoadMembersForStateEvents = true,
                        useThreadNotifications = true
                    )
                )
            )
        ).also { MatrixInstanceProvider.saveMatrixInstance(it) }

        val lastSession = try {
            MatrixInstanceProvider.matrix.authenticationService().getLastAuthenticatedSession()
        } catch (e: Exception) {
            null
        }

        lastSession?.let {
            val preferences = PreferencesProvider(context)
            if (preferences.getNotRestoredSessions().contains(it.sessionId)) {
                MainScope().launch(Dispatchers.IO) {
                    MatrixInstanceProvider.matrix.authenticationService()
                        .removeSession(it.sessionId)
                    preferences.removeSessionFromNotRestored(it.sessionId)
                }
            } else {
                startSession(it)
            }
        }
    }

    fun clearSession() {
        val session = currentSession ?: return
        if (session.syncService().isSyncThreadAlive()) session.close()
        clear()
    }

    fun removeListenersAndStopSync() {
        val session = currentSession ?: return
        session.syncService().stopSync()
        clear()
    }

    private fun clear() {
        currentSession?.removeListener(MatrixSessionListenerProvider.sessionListener)
        notificationSetupListener?.onStop()
        currentSession = null
    }

    fun startSession(
        session: Session,
        listener: Session.Listener? = null
    ) {
        listener?.let { session.addListener(it) }
        currentSession = session.apply {
            tryOrNull { open() }
            syncService().startSync(true)
        }
        session.addListener(MatrixSessionListenerProvider.sessionListener)
        notificationSetupListener?.onStartWithSession(session)
    }

    suspend fun awaitForSessionStart(session: Session) =
        suspendCancellableCoroutine {
            startSession(session, object : Session.Listener {
                override fun onSessionStarted(session: Session) {
                    super.onSessionStarted(session)
                    it.resume(session) { session.removeListener(this) }
                    onNewAuthLister?.invoke()
                }
            })
        }

    suspend fun awaitForSessionSync(session: Session) =
        suspendCancellableCoroutine {
            startSession(session, object : Session.Listener {
                override fun onStatisticsEvent(session: Session, statisticEvent: StatisticEvent) {
                    super.onStatisticsEvent(session, statisticEvent)
                    if (statisticEvent is StatisticEvent.InitialSyncRequest) {
                        it.resume(session) { session.removeListener(this) }
                        onNewAuthLister?.invoke()
                    }
                }
            })
        }
}