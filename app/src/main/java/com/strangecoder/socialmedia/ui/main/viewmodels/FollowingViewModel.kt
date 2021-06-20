package com.strangecoder.socialmedia.ui.main.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strangecoder.socialmedia.data.entities.User
import com.strangecoder.socialmedia.other.Event
import com.strangecoder.socialmedia.other.Resource
import com.strangecoder.socialmedia.repositories.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _user = MutableLiveData<Event<Resource<User>>>()
    val user: LiveData<Event<Resource<User>>> = _user

    fun getUser(uid: String) {
        _user.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getUser(uid)
            _user.postValue(Event(result))
        }
    }

    private val _usersFollowing = MutableLiveData<Event<Resource<List<User>>>>()
    val usersFollowing: LiveData<Event<Resource<List<User>>>> = _usersFollowing

    fun getUsers(uids: List<String>) {
        _usersFollowing.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getUsers(uids)
            _usersFollowing.postValue(Event(result))
        }
    }
}