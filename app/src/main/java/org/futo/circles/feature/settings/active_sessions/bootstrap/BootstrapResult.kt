package org.futo.circles.feature.settings.active_sessions.bootstrap

import org.matrix.android.sdk.api.failure.MatrixError
import org.matrix.android.sdk.api.session.securestorage.SsssKeyCreationInfo

sealed class BootstrapResult {

    data class Success(val keyInfo: SsssKeyCreationInfo) : BootstrapResult()
    abstract class Failure(val error: String?) : BootstrapResult()

    data class GenericError(val failure: Throwable) : Failure(failure.localizedMessage)
    data class InvalidPasswordError(val matrixError: MatrixError) : Failure(null)
    class FailedToCreateSSSSKey(failure: Throwable) : Failure(failure.localizedMessage)
    class FailedToSetDefaultSSSSKey(failure: Throwable) : Failure(failure.localizedMessage)
    class FailedToStorePrivateKeyInSSSS(failure: Throwable) : Failure(failure.localizedMessage)
    object MissingPrivateKey : Failure(null)
}