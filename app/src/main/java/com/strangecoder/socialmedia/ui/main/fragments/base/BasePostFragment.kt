package com.strangecoder.socialmedia.ui.main.fragments.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.strangecoder.socialmedia.other.EventObserver
import com.strangecoder.socialmedia.ui.main.adapters.PostsAdapter
import com.strangecoder.socialmedia.ui.main.dialogs.DeletePostDialog
import com.strangecoder.socialmedia.ui.main.viewmodels.BasePostViewModel
import com.strangecoder.socialmedia.ui.viewutils.snackBar
import javax.inject.Inject

abstract class BasePostFragment<T : ViewDataBinding> : Fragment() {

    protected lateinit var binding: T

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var postAdapter: PostsAdapter

    protected abstract val postProgressBar: ProgressBar

    protected abstract val basePostViewModel: BasePostViewModel

    private var currentLikedIndex: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            getFragmentView(),
            container,
            false
        )
        return binding.root
    }

    abstract fun getFragmentView(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()

        postAdapter.setOnLikeClickListener { post, i ->
            currentLikedIndex = i
            post.isLiked = !post.isLiked
            basePostViewModel.toggleLikeForPost(post)
        }
        postAdapter.setOnDeletePostClickListener { post ->
            DeletePostDialog().apply {
                setPositiveListener {
                    basePostViewModel.deletePost(post)
                }
            }.show(childFragmentManager, null)
        }
    }

    private fun subscribeToObservers() {
        basePostViewModel.likePostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                currentLikedIndex?.let { index ->
                    postAdapter.posts[index].isLiking = false
                    postAdapter.notifyItemChanged(index)
                }
                snackBar(it)
            },
            onLoading = {
                currentLikedIndex?.let { index ->
                    postAdapter.posts[index].isLiking = true
                    postAdapter.notifyItemChanged(index)
                }
            }
        ) { isLiked ->
            currentLikedIndex?.let { index ->
                val uid = FirebaseAuth.getInstance().uid!!
                postAdapter.posts[index].apply {
                    this.isLiked = isLiked
                    if (isLiked) likedBy += uid
                    else likedBy -= uid
                }
                postAdapter.notifyItemChanged(index)
            }
        })

        basePostViewModel.deletePostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { snackBar(it) }
        ) { deletedPost ->
            postAdapter.posts -= deletedPost
        })

        basePostViewModel.posts.observe(viewLifecycleOwner, EventObserver(
            onError = {
                postProgressBar.isVisible = false
                snackBar(it)
            },
            onLoading = {
                postProgressBar.isVisible = true
            }
        ) { posts ->
            postProgressBar.isVisible = false
            postAdapter.posts = posts
        })
    }

}