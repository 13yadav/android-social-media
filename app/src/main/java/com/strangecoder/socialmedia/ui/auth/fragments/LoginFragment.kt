package com.strangecoder.socialmedia.ui.auth.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.databinding.FragmentLoginBinding
import com.strangecoder.socialmedia.other.EventObserver
import com.strangecoder.socialmedia.ui.auth.AuthViewModel
import com.strangecoder.socialmedia.ui.main.MainActivity
import com.strangecoder.socialmedia.ui.snackBar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvRegisterNewAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        subscribeToObservers()
        binding.btnLogin.setOnClickListener {
            viewModel.loginUser(binding.etEmail.text.toString(), binding.etPassword.text.toString())
        }
    }

    private fun subscribeToObservers() {
        viewModel.loginStatus.observe(viewLifecycleOwner, EventObserver(
            onLoading = {
                binding.loginProgressBar.isVisible = true
            },
            onError = {
                binding.loginProgressBar.isVisible = false
                snackBar(it)
            },
            onSuccess = {
                binding.loginProgressBar.isVisible = false
                Intent(requireContext(), MainActivity::class.java).also {
                    startActivity(it)
                    requireActivity().finish()
                }
            }
        ))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}