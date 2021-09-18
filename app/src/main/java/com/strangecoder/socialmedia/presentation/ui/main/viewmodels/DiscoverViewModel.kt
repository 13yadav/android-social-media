package com.strangecoder.socialmedia.presentation.ui.main.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strangecoder.socialmedia.data.entities.User
import com.strangecoder.socialmedia.commons.Event
import com.strangecoder.socialmedia.commons.Resource
import com.strangecoder.socialmedia.domain.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _users = MutableLiveData<Event<Resource<List<User>>>>()
    val users: LiveData<Event<Resource<List<User>>>> = _users

    fun getAllUsers() {
        _users.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getAllUsers()
            _users.postValue(Event(result))
        }
    }
}