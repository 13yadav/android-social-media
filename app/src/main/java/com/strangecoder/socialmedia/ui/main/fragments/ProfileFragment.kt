package com.strangecoder.socialmedia.ui.main.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.databinding.FragmentProfileBinding
import com.strangecoder.socialmedia.other.EventObserver
import com.strangecoder.socialmedia.ui.main.fragments.base.BasePostFragment
import com.strangecoder.socialmedia.ui.main.viewmodels.ProfileViewModel
import com.strangecoder.socialmedia.ui.main.viewmodels.base.BasePostViewModel
import com.strangecoder.socialmedia.ui.viewutils.snackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class ProfileFragment : BasePostFragment<FragmentProfileBinding>() {

    override val postProgressBar: ProgressBar
        get() = binding.profilePostsProgressBar

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionLogout -> {
                FirebaseAuth.getInstance().signOut()
                findNavController().navigate(R.id.globalToLoginFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}