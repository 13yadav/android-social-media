package com.strangecoder.socialmedia.ui.main.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.google.firebase.firestore.FirebaseFirestore
import com.strangecoder.socialmedia.data.entities.Post
import com.strangecoder.socialmedia.data.paging.FollowPostsPagingSource
import com.strangecoder.socialmedia.other.Constants.PAGE_SIZE
import com.strangecoder.socialmedia.other.Event
import com.strangecoder.socialmedia.other.Resource
import com.strangecoder.socialmedia.repositories.MainRepository
import com.strangecoder.socialmedia.ui.main.viewmodels.base.BasePostViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : BasePostViewModel(repository, dispatcher) {

//    private val _posts = MutableLiveData<Event<Resource<List<Post>>>>()
//    override val posts: LiveData<Event<Resource<List<Post>>>>
//        get() = _posts
//
//    init {
//        getPosts()
//    }
//
//    override fun getPosts(uid: String) {
//        _posts.postValue(Event(Resource.Loading()))
//        viewModelScope.launch(dispatcher) {
//            val result = repository.getPostsForFollows()
//            _posts.postValue(Event(result))
//        }
//    }

    val pagingFlow = Pager(PagingConfig(PAGE_SIZE)) {
        FollowPostsPagingSource(FirebaseFirestore.getInstance())
    }.flow.cachedIn(viewModelScope)
}