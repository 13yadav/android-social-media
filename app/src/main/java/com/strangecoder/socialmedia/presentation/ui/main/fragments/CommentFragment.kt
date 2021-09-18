package com.strangecoder.socialmedia.presentation.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.databinding.FragmentCommentBinding
import com.strangecoder.socialmedia.commons.EventObserver
import com.strangecoder.socialmedia.presentation.ui.MainActivity
import com.strangecoder.socialmedia.presentation.ui.main.adapters.CommentAdapter
import com.strangecoder.socialmedia.presentation.ui.main.viewmodels.CommentViewModel
import com.strangecoder.socialmedia.presentation.viewutils.snackBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommentFragment : Fragment() {

    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var commentAdapter: CommentAdapter

    private val args: CommentFragmentArgs by navArgs()

    private val viewModel: CommentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        subscribeToObservers()
        viewModel.getCommentsForPost(args.postId)

        binding.btnComment.setOnClickListener {
            val commentText = binding.etComment.text.toString()
            viewModel.createComment(commentText, args.postId)
            binding.etComment.text?.clear()
        }
        commentAdapter.setOnDeleteCommentClickListener { comment ->
            viewModel.deleteComment(comment)
        }
        commentAdapter.setOnUserClickListener { comment ->
            if (FirebaseAuth.getInstance().uid!! == comment.uid) {
                (requireActivity() as MainActivity).bottomNav.selectedItemId = R.id.profileFragment
                return@setOnUserClickListener
            }
            findNavController().navigate(
                CommentFragmentDirections.actionGlobalToOthersProfileFragment(comment.uid)
            )
        }
    }

    private fun subscribeToObservers() {
        viewModel.commentsForPost.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.commentProgressBar.isVisible = false
                snackBar(it)
            },
            onLoading = { binding.commentProgressBar.isVisible = true }
        ) { comments ->
            binding.commentProgressBar.isVisible = false
            commentAdapter.comments = comments
        })

        viewModel.createCommentStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.commentProgressBar.isVisible = false
                snackBar(it)
                binding.btnComment.isEnabled = true
            },
            onLoading = {
                binding.commentProgressBar.isVisible = true
                binding.btnComment.isEnabled = false
            }
        ) { comment ->
            binding.commentProgressBar.isVisible = false
            binding.btnComment.isEnabled = true
            commentAdapter.comments += comment
        })

        viewModel.deleteCommentStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.commentProgressBar.isVisible = false
                snackBar(it)
            },
            onLoading = { binding.commentProgressBar.isVisible = true }
        ) { comment ->
            binding.commentProgressBar.isVisible = false
            commentAdapter.comments -= comment
        })
    }

    private fun setupRecyclerView() = binding.rvComments.apply {
        adapter = commentAdapter
        itemAnimator = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}