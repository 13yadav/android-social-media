package com.strangecoder.socialmedia.ui.main.fragments.base

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.other.EventObserver
import com.strangecoder.socialmedia.ui.MainActivity
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

    protected abstract val shimmerLayout: ShimmerFrameLayout

    protected abstract val recyclerView: RecyclerView

    protected abstract val errorTextView: TextView

    protected abstract val btnDiscover: Button

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

        postsAdapter.setOnUserClickListener { uid ->
            if (uid == FirebaseAuth.getInstance().uid!!) {
                (requireActivity() as MainActivity).bottomNav.selectedItemId = R.id.profileFragment
                return@setOnUserClickListener
            }
            findNavController().navigate(
                R.id.action_global_to_othersProfileFragment,
                Bundle().apply {
                    putString("uid", uid)
                })
        }

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

        postsAdapter.setOnLikedByClickListener { post ->
            basePostViewModel.getUsers(post.likedBy)
        }

        postsAdapter.setOnCommentsClickListener { comment ->
            findNavController().navigate(
                R.id.globalActionToCommentFragment,
                Bundle().apply { putString("postId", comment.id) }
            )
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
            val userAdapter = UserAdapter(glide).apply {
                setOnUserClickListener { user ->
                    if (user.uid == FirebaseAuth.getInstance().uid!!) {
                        (requireActivity() as MainActivity).bottomNav.selectedItemId =
                            R.id.profileFragment
                        return@setOnUserClickListener
                    }
                    findNavController().navigate(
                        R.id.action_global_to_othersProfileFragment,
                        Bundle().apply {
                            putString("uid", user.uid)
                        })
                }
            }
            userAdapter.users = users
            LikedByDialog(userAdapter).show(childFragmentManager, null)
        })

        basePostViewModel.deletePostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { snackBar(it) }
        ) { deletedPost ->
            postsAdapter.posts -= deletedPost
        })

        basePostViewModel.posts.observe(viewLifecycleOwner, EventObserver(
            onError = {
                shimmerLayout.stopShimmer()
                shimmerLayout.isVisible = false
                errorTextView.isVisible = true
                btnDiscover.isVisible = true
                snackBar(it)
            },
            onLoading = {
                shimmerLayout.isVisible = true
                errorTextView.isVisible = false
                btnDiscover.isVisible = false
            }
        ) { posts ->
            Log.d("rrLOG", posts.size.toString())
            if (posts.isNotEmpty()) {
                shimmerLayout.stopShimmer()
                shimmerLayout.isVisible = false
                errorTextView.isVisible = false
                btnDiscover.isVisible = false
                recyclerView.isVisible = true
                postsAdapter.posts = posts
            } else {
                shimmerLayout.stopShimmer()
                shimmerLayout.isVisible = false
                recyclerView.isVisible = false
                errorTextView.isVisible = true
                btnDiscover.isVisible = true
            }
        })
    }

    override fun onResume() {
        super.onResume()
        shimmerLayout.startShimmer()
    }

    override fun onPause() {
        super.onPause()
        shimmerLayout.stopShimmer()
    }

}