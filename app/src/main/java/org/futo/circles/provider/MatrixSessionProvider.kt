package org.futo.circles.provider

import android.content.Context
import kotlinx.coroutines.suspendCancellableCoroutine
import org.futo.circles.feature.notifications.GuardServiceStarter
import org.futo.circles.feature.notifications.PushRuleTriggerListener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.matrix.android.sdk.api.Matrix
import org.matrix.android.sdk.api.MatrixConfiguration
import org.matrix.android.sdk.api.extensions.tryOrNull
import org.matrix.android.sdk.api.session.Session
import org.matrix.android.sdk.api.session.statistics.StatisticEvent

object MatrixSessionProvider : KoinComponent {

    var currentSession: Session? = null
        private set

    private val guardServiceStarter: GuardServiceStarter by inject()
    private val pushRuleTriggerListener: PushRuleTriggerListener by inject()

    fun initSession(context: Context) {
        Matrix(
            context = context, matrixConfiguration = MatrixConfiguration(
                roomDisplayNameFallbackProvider = RoomDisplayNameFallbackProviderImpl(context)
            )
        ).also { MatrixInstanceProvider.saveMatrixInstance(it) }

        val lastSession = try {
            MatrixInstanceProvider.matrix.authenticationService().getLastAuthenticatedSession()
        } catch (e: Exception) {
            null
        }

        lastSession?.let { startSession(it) }
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
        pushRuleTriggerListener.stop()
        guardServiceStarter.stop()
        currentSession = null
    }

    fun startSession(
        session: Session,
        listener: Session.Listener? = null
    ) {
        listener?.let { session.addListener(it) }
        enableInviteKeysSharing(session)
        currentSession = session.apply {
            tryOrNull { open() }
            syncService().startSync(true)
        }
        session.addListener(MatrixSessionListenerProvider.sessionListener)
        pushRuleTriggerListener.startWithSession(session)
        guardServiceStarter.start()
    }

    suspend fun awaitForSessionStart(session: Session) =
        suspendCancellableCoroutine {
            startSession(session, object : Session.Listener {
                override fun onSessionStarted(session: Session) {
                    super.onSessionStarted(session)
                    it.resume(session) { session.removeListener(this) }
                }
            })
        }

    suspend fun awaitForSessionSync(session: Session) =
        suspendCancellableCoroutine {
            startSession(session, object : Session.Listener {
                override fun onStatisticsEvent(session: Session, statisticEvent: StatisticEvent) {
                    super.onStatisticsEvent(session, statisticEvent)
                    if (statisticEvent is StatisticEvent.InitialSyncRequest)
                        it.resume(session) { session.removeListener(this) }
                }
            })
        }

    //For Room history share
    private fun enableInviteKeysSharing(session: Session) {
        val isEnabled = session.cryptoService().isShareKeysOnInviteEnabled()
        if (!isEnabled) session.cryptoService().enableShareKeyOnInvite(true)
    }
}