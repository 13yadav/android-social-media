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
    lateinit var postsAdapter: PostsAdapter

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

        postsAdapter.setOnLikeClickListener { post, i ->
            currentLikedIndex = i
            post.isLiked = !post.isLiked
            basePostViewModel.toggleLikeForPost(post)
        }
        postsAdapter.setOnDeletePostClickListener { post ->
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
                    postsAdapter.posts[index].isLiking = false
                    postsAdapter.notifyItemChanged(index)
                }
                snackBar(it)
            },
            onLoading = {
                currentLikedIndex?.let { index ->
                    postsAdapter.posts[index].isLiking = true
                    postsAdapter.notifyItemChanged(index)
                }
            }
        ) { isLiked ->
            currentLikedIndex?.let { index ->
                val uid = FirebaseAuth.getInstance().uid!!
                postsAdapter.posts[index].apply {
                    this.isLiked = isLiked
                    if (isLiked) likedBy += uid
                    else likedBy -= uid
                }
                postsAdapter.notifyItemChanged(index)
            }
        })

        basePostViewModel.deletePostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { snackBar(it) }
        ) { deletedPost ->
            postsAdapter.posts -= deletedPost
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
            postsAdapter.posts = posts
        })
    }

}