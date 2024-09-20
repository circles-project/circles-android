package org.futo.circles.auth.extensions


import org.futo.circles.auth.model.TermsListItem
import org.matrix.android.sdk.api.auth.data.LocalizedFlowDataLoginTerms
import org.matrix.android.sdk.api.auth.registration.TermPolicies

fun TermPolicies.toTermsListItems() =
    toLoginTerms("en").mapIndexed { i, item ->
        TermsListItem(
            i,
            item.localizedName ?: item.policyName ?: "",
            item.localizedUrl ?: ""
        )
    }


fun TermPolicies.toLoginTerms(
    userLanguage: String = "en"
): List<LocalizedFlowDataLoginTerms> {
    val policies = (get("policies") as? Map<*, *>) ?: return emptyList()
    return policies.map {
        val tos = policies[it.key] as? Map<*, *> ?: return@map null
        ((tos[userLanguage]) as? Map<*, *>)?.let { termsMap ->
            val name = termsMap["name"] as? String
            val url = termsMap["url"] as? String
            LocalizedFlowDataLoginTerms(
                policyName = it.key.toString(),
                localizedUrl = url,
                localizedName = name,
                version = tos["version"] as? String
            )
        }
    }.filterNotNull()
}


