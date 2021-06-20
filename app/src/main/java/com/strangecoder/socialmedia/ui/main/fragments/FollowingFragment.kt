package com.strangecoder.socialmedia.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.databinding.FragmentFollowingBinding
import com.strangecoder.socialmedia.other.EventObserver
import com.strangecoder.socialmedia.ui.MainActivity
import com.strangecoder.socialmedia.ui.main.adapters.UserAdapter
import com.strangecoder.socialmedia.ui.main.viewmodels.FollowingViewModel
import com.strangecoder.socialmedia.ui.viewutils.snackBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowingFragment : Fragment() {

    private var _binding: FragmentFollowingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FollowingViewModel by viewModels()

    @Inject
    lateinit var followingAdapter: UserAdapter

    private val args: OthersProfileFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()
        setUpRecyclerView()

        viewModel.getUser(args.uid)

        followingAdapter.setOnUserClickListener { user ->
            if (user.uid == FirebaseAuth.getInstance().uid!!) {
                (requireActivity() as MainActivity).bottomNav.selectedItemId = R.id.profileFragment
                return@setOnUserClickListener
            }
            findNavController()
                .navigate(SearchFragmentDirections.actionGlobalToOthersProfileFragment(user.uid))
        }
    }

    private fun subscribeToObservers() {
        viewModel.user.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.progressBar.isVisible = false
                snackBar(it)
            },
            onLoading = { binding.progressBar.isVisible = true }
        ) { user ->
            viewModel.getUsers(user.follows)
        })

        viewModel.usersFollowing.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.progressBar.isVisible = false
                snackBar(it)
            },
            onLoading = {
                binding.progressBar.isVisible = true
            }
        ) { users ->
            binding.progressBar.isVisible = false
            followingAdapter.users = users
        })
    }

    private fun setUpRecyclerView() = binding.rvFollowing.apply {
        adapter = followingAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}