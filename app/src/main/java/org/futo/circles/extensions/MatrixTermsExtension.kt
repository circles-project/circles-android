package org.futo.circles.extensions


import org.futo.circles.model.TermsListItem
import org.matrix.android.sdk.api.auth.registration.TermPolicies
import org.matrix.android.sdk.api.auth.toLocalizedLoginTerms

fun TermPolicies.toTermsListItems() =
    toLocalizedLoginTerms("en").mapIndexed { i, item ->
        TermsListItem(
            i,
            item.localizedName ?: item.policyName ?: "",
            item.localizedUrl ?: ""
        )
    }

