package com.strangecoder.socialmedia.presentation.ui.main.fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.databinding.FragmentHomeBinding
import com.strangecoder.socialmedia.presentation.ui.main.fragments.base.BasePostFragment
import com.strangecoder.socialmedia.presentation.ui.main.viewmodels.HomeViewModel
import com.strangecoder.socialmedia.presentation.ui.main.viewmodels.base.BasePostViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BasePostFragment<FragmentHomeBinding>() {

    override val basePostViewModel: BasePostViewModel
        get() {
            val vm: HomeViewModel by viewModels()
            return vm
        }

    override val shimmerLayout: ShimmerFrameLayout
        get() = binding.shimmerFrameLayout

    override val recyclerView: RecyclerView
        get() = binding.rvAllPosts

    override val errorTextView: TextView
        get() = binding.tvError

    override val btnDiscover: Button
        get() = binding.btnDiscoverPeople

    override fun getFragmentView() = R.layout.fragment_home

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()

        binding.btnDiscoverPeople.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_discoverFragment)
        }
    }

    private fun setUpRecyclerView() = binding.rvAllPosts.apply {
        adapter = postsAdapter
        itemAnimator = null
    }
}