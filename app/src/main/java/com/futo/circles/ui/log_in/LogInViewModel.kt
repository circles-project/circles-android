package com.futo.circles.ui.log_in

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.futo.circles.ui.log_in.data_source.LoginDataSource
import kotlinx.coroutines.launch

class LogInViewModel(
    private val loginDataSource: LoginDataSource
) : ViewModel() {

    fun logIn(name: String, password: String, secondPassword: String?) {
        viewModelScope.launch { loginDataSource.logIn(name, password, secondPassword) }
    }
}