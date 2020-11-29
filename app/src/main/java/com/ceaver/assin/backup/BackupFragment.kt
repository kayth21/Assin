package com.ceaver.assin.backup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.backup_fragment.*

const val TITLE_FILE_NAME = "titles"
const val ACTION_FILE_NAME = "actions"
const val ALERT_FILE_NAME = "alerts"
const val INTENTION_FILE_NAME = "intentions"

class BackupFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.ceaver.assin.R.layout.backup_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        backupFragmentCreateBackupButton.setOnClickListener { findNavController().navigate(BackupFragmentDirections.actionBackupFragmentToBackupCreateFragment()) }
        backupFragmentRestoreBackupButton.setOnClickListener { findNavController().navigate(BackupFragmentDirections.actionBackupFragmentToBackupRestoreFragment()) }
        backupFragmentDeleteBackupButton.setOnClickListener { findNavController().navigate(BackupFragmentDirections.actionBackupFragmentToBackupDeleteFragment()) }
    }

    override fun onStop() {
        super.onStop()
        backupFragmentCreateBackupButton.setOnClickListener(null)
        backupFragmentRestoreBackupButton.setOnClickListener(null)
        backupFragmentDeleteBackupButton.setOnClickListener(null)
    }
}
