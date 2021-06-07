package com.strangecoder.socialmedia.ui.main.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.google.android.material.transition.MaterialSharedAxis
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.databinding.FragmentCreatePostBinding
import com.strangecoder.socialmedia.other.EventObserver
import com.strangecoder.socialmedia.ui.main.viewmodels.CreatePostViewModel
import com.strangecoder.socialmedia.ui.uiutils.snackBar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreatePostFragment : Fragment() {

    private var _binding: FragmentCreatePostBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CreatePostViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager

    private lateinit var cropContent: ActivityResultLauncher<String>

    private val cropActivityResultContract = object : ActivityResultContract<String, Uri?>() {
        override fun createIntent(context: Context, input: String?): Intent {
            return CropImage.activity()
                .setAspectRatio(4, 3)
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            if (intent == null) {
                snackBar("Failed to get image!")
                return null
            }
            return CropImage.getActivityResult(intent).uri
        }
    }

    private var curImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)

        cropContent = registerForActivityResult(cropActivityResultContract) {
            it?.let {
                viewModel.setCurImageIri(it)
            }
        }
    }

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

        binding.btnSetPostImage.setOnClickListener {
            cropContent.launch("image/*")
        }
        binding.ivPostImage.setOnClickListener {
            cropContent.launch("image/*")
        }
        binding.btnPost.setOnClickListener {
            curImageUri?.let { uri ->
                viewModel.createPost(uri, binding.etPostDescription.text.toString())
            } ?: snackBar(getString(R.string.error_no_image_chosen))
        }
    }

    private fun subscribeToObservers() {
        viewModel.curImageUri.observe(viewLifecycleOwner) {
            curImageUri = it
            binding.btnSetPostImage.isVisible = false
            glide.load(curImageUri).into(binding.ivPostImage)
        }

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