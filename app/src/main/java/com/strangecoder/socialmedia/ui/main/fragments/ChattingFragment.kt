package com.strangecoder.socialmedia.ui.main.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.strangecoder.socialmedia.data.entities.Message
import com.strangecoder.socialmedia.databinding.FragmentChattingBinding
import com.strangecoder.socialmedia.other.EventObserver
import com.strangecoder.socialmedia.ui.main.adapters.MessagesAdapter
import com.strangecoder.socialmedia.ui.main.viewmodels.ChatsViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ChattingFragment : Fragment() {

    private var _binding: FragmentChattingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChatsViewModel by viewModels()

    @Inject
    lateinit var messagesAdapter: MessagesAdapter

    private val args: ChattingFragmentArgs by navArgs()
    private val currentUserID = FirebaseAuth.getInstance().uid!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChattingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObservers()
        setUpRecyclerView()
        viewModel.loadMessages(currentUserID, args.uid)

        binding.btnSend.setOnClickListener {
            val msgContent = binding.etMessage.text.toString().trim()
            if (msgContent.isNotEmpty()) {
                val message = Message(
                    id = UUID.randomUUID().toString(),
                    content = msgContent,
                    idFrom = currentUserID,
                    idTo = args.uid,
                    read = false,
                    timestamp = System.currentTimeMillis().toString()
                )
                viewModel.sendMessage(message)
                binding.etMessage.text?.clear()
            }
        }
    }

    private fun subscribeToObservers() {
        viewModel.sentMessageStatus.observe(viewLifecycleOwner, EventObserver(
            onLoading = {
                //
            },
            onError = {
                //
            }
        ) { message ->
            messagesAdapter.messages += message
        })

        viewModel.messagesList.observe(viewLifecycleOwner, EventObserver(
            onLoading = {

            },
            onError = {

            }
        ) { messages ->
            messagesAdapter.messages += messages
        })
    }

    private fun setUpRecyclerView() = binding.messages.apply {
        adapter = messagesAdapter
        itemAnimator = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}