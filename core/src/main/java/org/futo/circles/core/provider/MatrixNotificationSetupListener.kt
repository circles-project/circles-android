package org.futo.circles.core.provider

import org.matrix.android.sdk.api.session.Session

interface MatrixNotificationSetupListener {

    fun onStartWithSession(session: Session)

    fun onStop()
}