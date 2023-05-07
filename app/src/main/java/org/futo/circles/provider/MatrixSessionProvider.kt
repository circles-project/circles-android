package org.futo.circles.provider

import android.content.Context
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import org.futo.circles.feature.notifications.GuardServiceStarter
import org.futo.circles.feature.notifications.PushRuleTriggerListener
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.matrix.android.sdk.api.Matrix
import org.matrix.android.sdk.api.MatrixConfiguration
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
                roomDisplayNameFallbackProvider = RoomDisplayNameFallbackProviderImpl(context),
                threadMessagesEnabledDefault = false
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
        session.removeListener(MatrixSessionListenerProvider.sessionListener)
        pushRuleTriggerListener.stop()
        guardServiceStarter.stop()
        currentSession = null
    }

    private fun startSession(session: Session, listener: Session.Listener? = null) {
        listener?.let { session.addListener(it) }
        enableInviteKeysSharing(session)
        Log.d("MyLog", "start session")
        currentSession = session.apply { open(); syncService().startSync(true) }
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