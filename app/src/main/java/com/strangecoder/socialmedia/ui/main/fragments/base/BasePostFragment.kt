package com.strangecoder.socialmedia.ui.main.fragments.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.bumptech.glide.RequestManager
import com.strangecoder.socialmedia.other.EventObserver
import com.strangecoder.socialmedia.ui.main.adapters.PostsAdapter
import com.strangecoder.socialmedia.ui.main.viewmodels.BasePostViewModel
import com.strangecoder.socialmedia.ui.viewutils.snackBar
import javax.inject.Inject

abstract class BasePostFragment<T : ViewDataBinding> : Fragment() {

    protected lateinit var binding: T

    @Inject
    lateinit var glide: RequestManager

    @Inject
    lateinit var postAdapter: PostsAdapter

    protected abstract val postProgressBar: ProgressBar

    protected abstract val basePostViewModel: BasePostViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            getFragmentView(),
            container,
            false
        )
        return binding.root
    }

    abstract fun getFragmentView(): Int

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        basePostViewModel.posts.observe(viewLifecycleOwner, EventObserver(
            onError = {
                postProgressBar.isVisible = false
                snackBar(it)
            },
            onLoading = {
                postProgressBar.isVisible = true
            },
            onSuccess = { posts ->
                postProgressBar.isVisible = false
                postAdapter.posts = posts
            }
        ))
    }

}