package org.futo.circles.extensions


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
): List<LocalizedFlowDataLoginTerms> =
    (get("policies") as? ArrayList<*>)?.mapNotNull { it ->
        val policy = (it as? Map<*, *>) ?: return@mapNotNull null
        val policyData = (policy[userLanguage] as? Map<*, *>) ?: return@mapNotNull null
        LocalizedFlowDataLoginTerms(
            policyName = policy["name"]?.toString(),
            version = policy["version"]?.toString(),
            localizedUrl = policyData["url"]?.toString(),
            localizedName = policyData["name"]?.toString()
        )
    } ?: emptyList()


