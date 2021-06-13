package com.strangecoder.socialmedia.ui.main.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.strangecoder.socialmedia.R
import com.strangecoder.socialmedia.ui.main.adapters.UserAdapter

class LikedByDialog(
    private val userAdapter: UserAdapter
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val rvLikedBy = RecyclerView(requireContext()).apply {
            adapter = userAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.liked_by_dialog_title)
            .setView(rvLikedBy)
            .create()
    }
}