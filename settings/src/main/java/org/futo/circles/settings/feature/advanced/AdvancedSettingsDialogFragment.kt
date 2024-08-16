package org.futo.circles.settings.feature.advanced

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.showNoInternetConnection
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.core.utils.LauncherActivityUtils
import org.futo.circles.settings.databinding.DialogFragmentAdvancedSettingsBinding

@AndroidEntryPoint
class AdvancedSettingsDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentAdvancedSettingsBinding>(
        DialogFragmentAdvancedSettingsBinding::inflate
    ) {

    private val preferencesProvider by lazy { PreferencesProvider(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        with(binding) {
            lDevMode.setOnClickListener { toggleDeveloperMode() }
            tvClearCache.setOnClickListener { clearCacheAndReload() }
            svDevMode.isChecked = preferencesProvider.isDeveloperModeEnabled()
        }
    }


    private fun clearCacheAndReload() {
        if (showNoInternetConnection()) return
        (activity as? AppCompatActivity)?.let {
            LauncherActivityUtils.clearCacheAndRestart(it)
        }
    }

    private fun toggleDeveloperMode() {
        val isEnabled = preferencesProvider.isDeveloperModeEnabled()
        preferencesProvider.setDeveloperMode(!isEnabled)
        binding.svDevMode.isChecked = !isEnabled
    }

}