package com.strangecoder.socialmedia.presentation.ui.main.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.databinding.FragmentProfileBinding
import com.strangecoder.socialmedia.commons.EventObserver
import com.strangecoder.socialmedia.presentation.ui.main.fragments.base.BasePostFragment
import com.strangecoder.socialmedia.presentation.ui.main.viewmodels.ProfileViewModel
import com.strangecoder.socialmedia.presentation.ui.main.viewmodels.base.BasePostViewModel
import com.strangecoder.socialmedia.presentation.viewutils.snackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class ProfileFragment : BasePostFragment<FragmentProfileBinding>() {

    override val basePostViewModel: BasePostViewModel
        get() {
            val vm: ProfileViewModel by viewModels()
            return vm
        }

    override val shimmerLayout: ShimmerFrameLayout
        get() = binding.shimmerFrameLayout

    override val recyclerView: RecyclerView
        get() = binding.rvPosts

    override val errorTextView: TextView
        get() = binding.tvError

    override val btnDiscover: Button
        get() = binding.btnDiscover

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
            onLoading = {},
            onError = {
                snackBar(it)
            }
        ) { user ->
            binding.tvUsername.text = user.username
            binding.tvProfileDescription.text =
                if (user.bio.isEmpty()) requireContext().getString(R.string.no_description)
                else user.bio
            glide.load(user.profilePictureUrl).into(binding.ivProfileImage)
            binding.followingCount.text = user.follows.size.toString()
            binding.followingCount.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.globalToFollowingFragment(user.uid)
                )
            }
            binding.followingLabel.setOnClickListener {
                findNavController().navigate(
                    ProfileFragmentDirections.globalToFollowingFragment(user.uid)
                )
            }
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