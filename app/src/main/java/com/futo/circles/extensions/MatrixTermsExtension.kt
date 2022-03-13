package com.futo.circles.extensions


import com.futo.circles.core.DEFAULT_TERMS_NAME
import com.futo.circles.model.TermsListItem
import org.matrix.android.sdk.api.auth.registration.TermPolicies
import org.matrix.android.sdk.internal.auth.registration.LocalizedFlowDataLoginTerms


private fun TermPolicies.toLocalizedLoginTerms(
    language: String = "en"
): List<LocalizedFlowDataLoginTerms> {
    val result = ArrayList<LocalizedFlowDataLoginTerms>()

    val policies = get("policies")
    if (policies is Map<*, *>) {
        policies.keys.forEach { policyName ->
            val localizedFlowDataLoginTerms = LocalizedFlowDataLoginTerms()
            localizedFlowDataLoginTerms.policyName = policyName as String

            val policy = policies[policyName]

            // Enter this policy
            if (policy is Map<*, *>) {
                // Version
                localizedFlowDataLoginTerms.version = policy["version"] as String?

                var defaultLanguageUrlAndName: UrlAndName? = null
                var firstUrlAndName: UrlAndName? = null

                // Search for language
                policy.keys.forEach { policyKey ->
                    when (policyKey) {
                        "version" -> Unit // Ignore
                        language -> {
                            // We found default language
                            defaultLanguageUrlAndName = extractUrlAndName(policy[policyKey])
                        }
                        else -> {
                            if (firstUrlAndName == null) {
                                // Get at least some data
                                firstUrlAndName = extractUrlAndName(policy[policyKey])
                            }
                        }
                    }
                }

                // Copy found language data by priority
                when {
                    defaultLanguageUrlAndName != null -> {
                        localizedFlowDataLoginTerms.localizedUrl = defaultLanguageUrlAndName!!.url
                        localizedFlowDataLoginTerms.localizedName = defaultLanguageUrlAndName!!.name
                    }
                    firstUrlAndName != null -> {
                        localizedFlowDataLoginTerms.localizedUrl = firstUrlAndName!!.url
                        localizedFlowDataLoginTerms.localizedName = firstUrlAndName!!.name
                    }
                }
            }

            result.add(localizedFlowDataLoginTerms)
        }
    }

    return result
}

private fun extractUrlAndName(policyData: Any?): UrlAndName? {
    if (policyData is Map<*, *>) {
        val url = policyData["url"] as String?
        val name = policyData["name"] as String?

        if (url != null && name != null) {
            return UrlAndName(url, name)
        }
    }
    return null
}

data class UrlAndName(
    val url: String,
    val name: String
)

fun TermPolicies.toTermsListItems() =
    toLocalizedLoginTerms().mapIndexed { i, item ->
        TermsListItem(
            i,
            item.localizedName ?: item.policyName ?: DEFAULT_TERMS_NAME,
            item.localizedUrl ?: ""
        )
    }

