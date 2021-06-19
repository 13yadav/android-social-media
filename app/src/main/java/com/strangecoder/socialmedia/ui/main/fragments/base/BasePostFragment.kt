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
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.other.EventObserver
import com.strangecoder.socialmedia.ui.main.adapters.PostsAdapter
import com.strangecoder.socialmedia.ui.main.adapters.UserAdapter
import com.strangecoder.socialmedia.ui.main.dialogs.DeletePostDialog
import com.strangecoder.socialmedia.ui.main.dialogs.LikedByDialog
import com.strangecoder.socialmedia.ui.main.viewmodels.base.BasePostViewModel
import com.strangecoder.socialmedia.ui.viewutils.snackBar
import javax.inject.Inject

abstract class BasePostFragment<T : ViewDataBinding> : Fragment() {

    protected lateinit var binding: T

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var postsAdapter: PostsAdapter

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
        postsAdapter.setOnDeletePostClickListener { post, position ->
            DeletePostDialog().apply {
                setPositiveListener {
                    basePostViewModel.deletePost(post, position)
                }
            }.show(childFragmentManager, null)
        }

        postsAdapter.setOnLikedByClickListener { post ->
            basePostViewModel.getUsers(post.likedBy)
        }

        postsAdapter.setOnCommentsClickListener { comment ->
            findNavController().navigate(
                R.id.globalActionToCommentDialog,
                Bundle().apply { putString("postId", comment.id) }
            )
        }
    }

    private fun subscribeToObservers() {
        basePostViewModel.likePostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                currentLikedIndex?.let { index ->
                    postsAdapter.peek(index)?.isLiking = false
                    postsAdapter.notifyItemChanged(index)
                }
                snackBar(it)
            },
            onLoading = {
                currentLikedIndex?.let { index ->
                    postsAdapter.peek(index)?.isLiking = true
                    postsAdapter.notifyItemChanged(index)
                }
            }
        ) { isLiked ->
            currentLikedIndex?.let { index ->
                val uid = FirebaseAuth.getInstance().uid!!
                postsAdapter.peek(index)?.apply {
                    this.isLiked = isLiked
                    isLiking = false
                    if (isLiked) likedBy += uid
                    else likedBy -= uid
                }
                postsAdapter.notifyItemChanged(index)
            }
        })

        basePostViewModel.likedByUsers.observe(viewLifecycleOwner, EventObserver(
            onError = { snackBar(it) }
        ) { users ->
            val userAdapter = UserAdapter(glide)
            userAdapter.users = users
            LikedByDialog(userAdapter).show(childFragmentManager, null)
        })

//        basePostViewModel.posts.observe(viewLifecycleOwner, EventObserver(
//            onError = {
//                postProgressBar.isVisible = false
//                snackBar(it)
//            },
//            onLoading = {
//                postProgressBar.isVisible = true
//            }
//        ) { posts ->
//            postProgressBar.isVisible = false
//            postsAdapter.posts = posts
//        })
    }

}