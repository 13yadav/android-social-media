package com.strangecoder.socialmedia.ui.main.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.google.firebase.auth.FirebaseAuth
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.databinding.FragmentProfileBinding
import com.strangecoder.socialmedia.other.EventObserver
import com.strangecoder.socialmedia.ui.main.fragments.base.BasePostFragment
import com.strangecoder.socialmedia.ui.main.viewmodels.ProfileViewModel
import com.strangecoder.socialmedia.ui.main.viewmodels.base.BasePostViewModel
import com.strangecoder.socialmedia.ui.viewutils.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
open class ProfileFragment : BasePostFragment<FragmentProfileBinding>() {

    override val basePostViewModel: BasePostViewModel
        get() {
            val vm: ProfileViewModel by viewModels()
            return vm
        }

    override fun getFragmentView() = R.layout.fragment_profile

    protected val viewModel: ProfileViewModel
        get() = basePostViewModel as ProfileViewModel

    protected open val uid: String
        get() = FirebaseAuth.getInstance().uid!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        subscribeToObservers()

        binding.btnToggleFollow.isVisible = false
        binding.btnMessage.isVisible = false
        binding.btnEditProfile.isVisible = true

        viewModel.loadProfile(uid)

        binding.btnEditProfile.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment)
        }

        lifecycleScope.launch {
            viewModel.getPagingFlow(uid).collect {
                postsAdapter.submitData(it)
            }
        }
        lifecycleScope.launch {
            postsAdapter.loadStateFlow.collectLatest {
                binding.profilePostsProgressBar.isVisible =
                    it.refresh is LoadState.Loading || it.append is LoadState.Loading
            }
        }
    }

    private fun setupRecyclerView() = binding.rvPosts.apply {
        adapter = postsAdapter
        itemAnimator = null
    }

    private fun subscribeToObservers() {
        viewModel.profileMeta.observe(viewLifecycleOwner, EventObserver(
            onLoading = {
                binding.profilePostsProgressBar.isVisible = true
            },
            onError = {
                binding.profilePostsProgressBar.isVisible = false
                snackBar(it)
            }
        ) { user ->
            binding.profilePostsProgressBar.isVisible = false
            binding.tvUsername.text = user.username
            binding.tvProfileDescription.text =
                if (user.bio.isEmpty()) requireContext().getString(R.string.no_description)
                else user.bio
            glide.load(user.profilePictureUrl).into(binding.ivProfileImage)
        })

        basePostViewModel.deletePostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { snackBar(it) }
        ) {
            postsAdapter.refresh()
            if (viewModel.deletePostPosition != -1)
                postsAdapter.notifyItemChanged(viewModel.deletePostPosition)
            viewModel.invalidatePagingSource()
        })
    }
}