package com.strangecoder.socialmedia.ui.main.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.databinding.FragmentHomeBinding
import com.strangecoder.socialmedia.ui.main.fragments.base.BasePostFragment
import com.strangecoder.socialmedia.ui.main.viewmodels.HomeViewModel
import com.strangecoder.socialmedia.ui.main.viewmodels.base.BasePostViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : BasePostFragment<FragmentHomeBinding>() {

    override val basePostViewModel: BasePostViewModel
        get() {
            val vm: HomeViewModel by viewModels()
            return vm
        }

    private val viewModel: HomeViewModel by lazy {
        basePostViewModel as HomeViewModel
    }

    override fun getFragmentView() = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        lifecycleScope.launch {
            viewModel.pagingFlow.collect {
                postsAdapter.submitData(it)
            }
        }
        lifecycleScope.launch {
            postsAdapter.loadStateFlow.collectLatest {
                binding.allPostsProgressBar.isVisible =
                    it.refresh is LoadState.Loading || it.append is LoadState.Loading
            }
        }
    }

    private fun setUpRecyclerView() = binding.rvAllPosts.apply {
        adapter = postsAdapter
        itemAnimator = null
    }
}