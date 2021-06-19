package com.strangecoder.socialmedia.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.databinding.FragmentSearchBinding
import com.strangecoder.socialmedia.other.Constants.SEARCH_TIME_DELAY
import com.strangecoder.socialmedia.other.EventObserver
import com.strangecoder.socialmedia.ui.MainActivity
import com.strangecoder.socialmedia.ui.main.adapters.UserAdapter
import com.strangecoder.socialmedia.ui.main.viewmodels.SearchViewModel
import com.strangecoder.socialmedia.ui.viewutils.snackBar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var userAdapter: UserAdapter

    private val viewModel: SearchViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        subscribeToObservers()

        var job: Job? = null
        binding.etSearch.addTextChangedListener { editable ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(SEARCH_TIME_DELAY)
                editable?.let {
                    viewModel.searchUser(it.toString())
                }
            }
        }

        userAdapter.setOnUserClickListener { user ->
            if (user.uid == FirebaseAuth.getInstance().uid!!) {
                (requireActivity() as MainActivity).bottomNav.selectedItemId = R.id.profileFragment
                return@setOnUserClickListener
            }
            findNavController()
                .navigate(SearchFragmentDirections.actionGlobalToOthersProfileFragment(user.uid))
        }
    }

    private fun subscribeToObservers() {
        viewModel.searchResults.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.searchProgressBar.isVisible = false
                snackBar(it)
            },
            onLoading = {
                binding.searchProgressBar.isVisible = true
            }
        ) { users ->
            binding.searchProgressBar.isVisible = false
            userAdapter.users = users
        })
    }

    private fun setupRecyclerView() = binding.rvSearchResults.apply {
        adapter = userAdapter
        itemAnimator = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}