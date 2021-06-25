package com.strangecoder.socialmedia.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.databinding.FragmentMessagesBinding
import com.strangecoder.socialmedia.other.EventObserver
import com.strangecoder.socialmedia.ui.main.adapters.UserAdapter
import com.strangecoder.socialmedia.ui.main.viewmodels.FollowingViewModel
import com.strangecoder.socialmedia.ui.viewutils.snackBar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatListFragment : Fragment() {

    private var _binding: FragmentMessagesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FollowingViewModel by viewModels()

    @Inject
    lateinit var chatsAdapter: UserAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMessagesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()
        setUpRecyclerView()
        viewModel.getUser(FirebaseAuth.getInstance().uid!!)

        chatsAdapter.setOnUserClickListener { user ->
            findNavController().navigate(
                R.id.actionGlobalToChattingFragment,
                Bundle().apply { putString("uid", user.uid) })
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
            chatsAdapter.users = users
        })
    }

    private fun setUpRecyclerView() = binding.chatList.apply {
        adapter = chatsAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}