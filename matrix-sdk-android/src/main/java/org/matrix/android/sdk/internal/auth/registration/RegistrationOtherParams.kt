package org.matrix.android.sdk.internal.auth.registration

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.matrix.android.sdk.api.util.JsonDict

/**
 * Class to pass parameters to the custom registration types for /register.
 */
@JsonClass(generateAdapter = true)
internal data class RegistrationOtherParams(
    // authentication parameters
    @Json(name = "auth")
    val auth: JsonDict? = null,
)