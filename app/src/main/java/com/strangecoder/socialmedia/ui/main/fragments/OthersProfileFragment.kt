package com.strangecoder.socialmedia.ui.main.fragments

import android.graphics.Color
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.view.View
import android.view.animation.OvershootInterpolator
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.data.entities.User
import com.strangecoder.socialmedia.other.EventObserver

class OthersProfileFragment : ProfileFragment() {

    private val args: OthersProfileFragmentArgs by navArgs()

    override val uid: String
        get() = args.uid

    private var currentUser: User? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()
        binding.btnToggleFollow.setOnClickListener {
            viewModel.toggleFollowForUser(uid)
        }
    }

    private fun subscribeToObservers() {
        viewModel.profileMeta.observe(viewLifecycleOwner, EventObserver {
            binding.btnToggleFollow.isVisible = true
            setupToggleFollowBtn(it)
            currentUser = it
        })
        viewModel.followStatus.observe(viewLifecycleOwner, EventObserver {
            currentUser?.isFollowing = it
            setupToggleFollowBtn(currentUser ?: return@EventObserver)
        })
    }

    private fun setupToggleFollowBtn(user: User) {
        binding.btnToggleFollow.apply {
            val changeBounds = ChangeBounds().apply {
                duration = 300
                interpolator = OvershootInterpolator()
            }
            val set1 = ConstraintSet()
            val set2 = ConstraintSet()
            set1.clone(requireContext(), R.layout.fragment_profile)
            set2.clone(requireContext(), R.layout.fragment_profile_anim)
            TransitionManager.beginDelayedTransition(binding.clProfile, changeBounds)
            if (user.isFollowing) {
                text = requireContext().getString(R.string.unfollow)
                setBackgroundColor(Color.RED)
                setTextColor(Color.WHITE)
                set1.applyTo(binding.clProfile)
            } else {
                text = requireContext().getString(R.string.follow)
                setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondaryColor))
                setTextColor(Color.BLACK)
                set2.applyTo(binding.clProfile)
            }
        }
    }
}