package com.strangecoder.socialmedia.ui.auth

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.other.Constants.MAX_USERNAME_LENGTH
import com.strangecoder.socialmedia.other.Constants.MIN_PASSWORD_LENGTH
import com.strangecoder.socialmedia.other.Constants.MIN_USERNAME_LENGTH
import com.strangecoder.socialmedia.other.Event
import com.strangecoder.socialmedia.other.Resource
import com.strangecoder.socialmedia.repositories.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _registerStatus = MutableLiveData<Event<Resource<AuthResult>>>()
    val registerStatus: LiveData<Event<Resource<AuthResult>>> = _registerStatus

    fun registerUser(email: String, username: String, password: String, confirmPassword: String) {
        val error = if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            applicationContext.getString(R.string.error_input_empty)
        } else if (password != confirmPassword) {
            applicationContext.getString(R.string.error_incorrectly_repeated_password)
        } else if (username.length < MIN_USERNAME_LENGTH) {
            applicationContext.getString(R.string.error_username_too_short, MIN_USERNAME_LENGTH)
        } else if (username.length > MAX_USERNAME_LENGTH) {
            applicationContext.getString(R.string.error_username_too_long, MAX_USERNAME_LENGTH)
        } else if (password.length < MIN_PASSWORD_LENGTH) {
            applicationContext.getString(R.string.error_password_too_short, MIN_PASSWORD_LENGTH)
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            applicationContext.getString(R.string.error_not_a_valid_email)
        } else null

        error?.let {
            _registerStatus.postValue(Event(Resource.Error(it)))
            return
        }
        _registerStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.registerUser(email, username, password)
            _registerStatus.postValue(Event(result))
        }
    }

    private val _loginStatus = MutableLiveData<Event<Resource<AuthResult>>>()
    val loginStatus: LiveData<Event<Resource<AuthResult>>> = _loginStatus

    fun loginUser(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            val error = applicationContext.getString(R.string.error_input_empty)
            _loginStatus.postValue(Event(Resource.Error(error)))
        } else {
            _loginStatus.postValue(Event(Resource.Loading()))
            viewModelScope.launch(dispatcher) {
                val result = repository.loginUser(email, password)
                _loginStatus.postValue(Event(result))
            }
        }
    }

}