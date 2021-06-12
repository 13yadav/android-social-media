package com.strangecoder.socialmedia.ui.main.fragments

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.fragment.app.viewModels
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.databinding.FragmentHomeBinding
import com.strangecoder.socialmedia.ui.main.fragments.base.BasePostFragment
import com.strangecoder.socialmedia.ui.main.viewmodels.BasePostViewModel
import com.strangecoder.socialmedia.ui.main.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BasePostFragment<FragmentHomeBinding>() {

    override val postProgressBar: ProgressBar
        get() = binding.allPostsProgressBar

    override val basePostViewModel: BasePostViewModel
        get() {
            val vm: HomeViewModel by viewModels()
            return vm
        }

    override fun getFragmentView() = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() = binding.rvAllPosts.apply {
        adapter = postAdapter
        itemAnimator = null
    }
}