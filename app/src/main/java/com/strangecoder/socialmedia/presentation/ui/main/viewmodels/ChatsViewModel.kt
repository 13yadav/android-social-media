package com.strangecoder.socialmedia.presentation.ui.main.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.strangecoder.socialmedia.data.entities.Message
import com.strangecoder.socialmedia.commons.Event
import com.strangecoder.socialmedia.commons.Resource
import com.strangecoder.socialmedia.domain.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatsViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _sentMessage = MutableLiveData<Event<Resource<Message>>>()
    val sentMessageStatus: LiveData<Event<Resource<Message>>> = _sentMessage

    fun sendMessage(message: Message) {
        _sentMessage.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.sendMessage(message)
            _sentMessage.postValue(Event(result))
        }
    }

    private val _messagesList = MutableLiveData<Event<Resource<List<Message>>>>()
    val messagesList: LiveData<Event<Resource<List<Message>>>> = _messagesList

    fun loadMessages(idFrom: String, idTo: String) {
        _messagesList.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.loadMessages(idFrom, idTo)
            _messagesList.postValue(Event(result))
        }
    }

}