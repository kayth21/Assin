package com.ceaver.assin.backup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ceaver.assin.databinding.BackupFragmentBinding

const val TITLE_FILE_NAME = "titles"
const val ACTION_FILE_NAME = "actions"
const val ALERT_FILE_NAME = "alerts"
const val INTENTION_FILE_NAME = "intentions"

class BackupFragment : Fragment() {

    private lateinit var binding: BackupFragmentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BackupFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        binding.backupFragmentCreateBackupButton.setOnClickListener { findNavController().navigate(BackupFragmentDirections.actionBackupFragmentToBackupCreateFragment()) }
        binding.backupFragmentRestoreBackupButton.setOnClickListener { findNavController().navigate(BackupFragmentDirections.actionBackupFragmentToBackupRestoreFragment()) }
        binding.backupFragmentDeleteBackupButton.setOnClickListener { findNavController().navigate(BackupFragmentDirections.actionBackupFragmentToBackupDeleteFragment()) }
    }

    override fun onStop() {
        super.onStop()
        binding.backupFragmentCreateBackupButton.setOnClickListener(null)
        binding.backupFragmentRestoreBackupButton.setOnClickListener(null)
        binding.backupFragmentDeleteBackupButton.setOnClickListener(null)
    }
}
