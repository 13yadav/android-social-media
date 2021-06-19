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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.data.entities.ProfileUpdate
import com.strangecoder.socialmedia.databinding.FragmentEditProfileBinding
import com.strangecoder.socialmedia.other.EventObserver
import com.strangecoder.socialmedia.ui.main.viewmodels.EditProfileViewModel
import com.strangecoder.socialmedia.ui.viewutils.snackBar
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: EditProfileViewModel by viewModels()

    @Inject
    lateinit var glide: RequestManager

    private var curImageUri: Uri? = null

    private lateinit var cropContent: ActivityResultLauncher<Any?>

    private val cropActivityResultContract = object : ActivityResultContract<Any?, Uri?>() {
        override fun createIntent(context: Context, input: Any?): Intent {
            return CropImage.activity()
                .setAspectRatio(1, 1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(requireContext())
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            if (intent == null) {
                snackBar("Failed to get image!")
                return null
            }
            return CropImage.getActivityResult(intent)?.uri
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cropContent = registerForActivityResult(cropActivityResultContract) { uri ->
            uri?.let {
                viewModel.setCurImageUri(it)
                binding.btnUpdateProfile.isEnabled = true
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()
        val uid = FirebaseAuth.getInstance().uid!!
        viewModel.getUser(uid)
        binding.btnUpdateProfile.isEnabled = false
        binding.etUsername.addTextChangedListener {
            binding.btnUpdateProfile.isEnabled = true
        }
        binding.etDescription.addTextChangedListener {
            binding.btnUpdateProfile.isEnabled = true
        }

        binding.ivProfileImage.setOnClickListener {
            cropContent.launch(null)
        }

        binding.btnUpdateProfile.setOnClickListener {
            it.isVisible = false
            binding.editProfileProgressBar.isVisible = true
            val username = binding.etUsername.text.toString()
            val bio = binding.etDescription.text.toString()
            val profileUpdate = ProfileUpdate(uid, username, bio, curImageUri)
            viewModel.updateProfile(profileUpdate)
        }
    }

    private fun subscribeToObservers() {
        viewModel.getUserStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.editProfileProgressBar.isVisible = false
                snackBar(it)
            },
            onLoading = { binding.editProfileProgressBar.isVisible = true }
        ) { user ->
            binding.editProfileProgressBar.isVisible = false
            glide.load(user.profilePictureUrl).into(binding.ivProfileImage)
            binding.etUsername.setText(user.username)
            binding.etDescription.setText(user.bio)
            binding.btnUpdateProfile.isEnabled = false
        })

        viewModel.curImageUri.observe(viewLifecycleOwner) { uri ->
            curImageUri = uri
            glide.load(uri).into(binding.ivProfileImage)
        }

        viewModel.updateProfileStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.editProfileProgressBar.isVisible = false
                snackBar(it)
                binding.btnUpdateProfile.isEnabled = true
            },
            onLoading = {
                binding.editProfileProgressBar.isVisible = true
                binding.btnUpdateProfile.isEnabled = false
            }
        ) {
            binding.editProfileProgressBar.isVisible = false
            binding.btnUpdateProfile.isEnabled = false
            snackBar(requireContext().getString(R.string.profile_updated))
            findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}