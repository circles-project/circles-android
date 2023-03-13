package org.futo.circles.di.data_source

import org.futo.circles.core.matrix.pass_phrase.create.CreatePassPhraseDataSource
import org.futo.circles.core.matrix.pass_phrase.restore.RestoreBackupDataSource
import org.futo.circles.core.matrix.pass_phrase.restore.SSSSRestoreDataSource
import org.futo.circles.feature.log_in.switch_user.SwitchUserDataSource
import org.futo.circles.feature.settings.SettingsDataSource
import org.futo.circles.feature.settings.active_sessions.ActiveSessionsDataSource
import org.futo.circles.feature.settings.change_password.ChangePasswordDataSource
import org.futo.circles.feature.sign_up.username.UsernameDataSource
import org.futo.circles.provider.PreferencesProvider
import org.koin.dsl.module

val settingsDSModule = module {
    factory { PreferencesProvider(get()) }
    factory { SwitchUserDataSource() }
    factory { SettingsDataSource(get(), get(), get()) }
    factory { CreatePassPhraseDataSource(get()) }
    factory { RestoreBackupDataSource(get(), get()) }
    factory { SSSSRestoreDataSource() }
    factory { ChangePasswordDataSource(get(), get()) }
    factory { ActiveSessionsDataSource(get(), get()) }
    factory { UsernameDataSource(get()) }
}