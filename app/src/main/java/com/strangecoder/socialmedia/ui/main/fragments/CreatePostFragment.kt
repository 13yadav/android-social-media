package com.strangecoder.socialmedia.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.strangecoder.socialmedia.databinding.FragmentCreatePostBinding
import com.strangecoder.socialmedia.other.EventObserver
import com.strangecoder.socialmedia.ui.main.viewmodels.CreatePostViewModel
import com.strangecoder.socialmedia.ui.uiutils.snackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreatePostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreatePostBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
    }

    private fun subscribeToObservers() {
        viewModel.createPostStatus.observe(viewLifecycleOwner, EventObserver(
            onLoading = {
                binding.createPostProgressBar.isVisible = true
            },
            onError = {
                binding.createPostProgressBar.isVisible = false
                snackBar(it)
            },
            onSuccess = {
                binding.createPostProgressBar.isVisible = false
                findNavController().popBackStack()
            }
        ))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}