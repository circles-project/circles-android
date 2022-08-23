package org.futo.circles.feature.log_in.stages

import org.futo.circles.core.matrix.pass_phrase.restore.RestorePassPhraseDataSource
import org.futo.circles.core.matrix.room.CoreSpacesTreeBuilder
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.createResult

class LoginStagesDataSource(
    private val restorePassPhraseDataSource: RestorePassPhraseDataSource,
    private val coreSpacesTreeBuilder: CoreSpacesTreeBuilder
) {

    val passPhraseLoadingLiveData = restorePassPhraseDataSource.loadingLiveData

    suspend fun restoreKeys(password: String) = createResult {
        restorePassPhraseDataSource.restoreKeysWithPassPhase(password)
    }

    suspend fun getEncryptionAlgorithm(): String? = when (val algorithmResult =
        createResult { restorePassPhraseDataSource.getEncryptionAlgorithm() }) {
        is Response.Error -> null
        is Response.Success -> algorithmResult.data
    }

    suspend fun createSpacesTree() = createResult {
        coreSpacesTreeBuilder.createCoreSpacesTree()
    }

    fun isCirclesTreeCreated() = coreSpacesTreeBuilder.isCirclesHierarchyCreated()
}