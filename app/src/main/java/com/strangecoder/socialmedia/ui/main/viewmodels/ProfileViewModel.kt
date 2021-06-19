package com.strangecoder.socialmedia.ui.main.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.firebase.firestore.FirebaseFirestore
import com.strangecoder.socialmedia.data.entities.Post
import com.strangecoder.socialmedia.data.entities.User
import com.strangecoder.socialmedia.data.paging.ProfilePostsPagingSource
import com.strangecoder.socialmedia.other.Constants.PAGE_SIZE
import com.strangecoder.socialmedia.other.Event
import com.strangecoder.socialmedia.other.Resource
import com.strangecoder.socialmedia.repositories.MainRepository
import com.strangecoder.socialmedia.ui.main.viewmodels.base.BasePostViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : BasePostViewModel(repository, dispatcher) {

    private val _profileMeta = MutableLiveData<Event<Resource<User>>>()
    val profileMeta: LiveData<Event<Resource<User>>> get() = _profileMeta

    private val _followStatus = MutableLiveData<Event<Resource<Boolean>>>()
    val followStatus: LiveData<Event<Resource<Boolean>>> get() = _followStatus

//    private val _posts = MutableLiveData<Event<Resource<List<Post>>>>()
//    override val posts: LiveData<Event<Resource<List<Post>>>> get() = _posts

    private var pagingSource: ProfilePostsPagingSource? = null

    fun getPagingFlow(uid: String): Flow<PagingData<Post>> {
        pagingSource = ProfilePostsPagingSource(FirebaseFirestore.getInstance(), uid)
        return Pager(PagingConfig(PAGE_SIZE)) {
            pagingSource!!
        }.flow.cachedIn(viewModelScope)
    }

    fun invalidatePagingSource() {
        pagingSource?.invalidate()
    }

//    override fun getPosts(uid: String) {
//        _posts.postValue(Event(Resource.Loading()))
//        viewModelScope.launch(dispatcher) {
//            val result = repository.getPostForProfile(uid)
//            _posts.postValue(Event(result))
//        }
//    }

    fun toggleFollowForUser(uid: String) {
        _followStatus.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.toggleFollowForUser(uid)
            _followStatus.postValue(Event(result))
        }
    }

    fun loadProfile(uid: String) {
        _profileMeta.postValue(Event(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getUser(uid)
            _profileMeta.postValue(Event(result))
        }
//        getPosts(uid)
    }
}