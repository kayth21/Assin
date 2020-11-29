package com.ceaver.assin.backup.restore

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ceaver.assin.R
import com.ceaver.assin.databinding.BackupRestoreFragmentBinding
import kotlinx.android.synthetic.main.backup_restore_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BackupRestoreFragment : Fragment() {
    private lateinit var viewModel: BackupRestoreViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<BackupRestoreViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: BackupRestoreFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.backup_restore_fragment, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.existingBackups.isEmpty()) {
            backupRestoreFragmentRestoreBackupButton.text = "No backups found"
        } else {
            viewModel.existingBackups.forEach {
                val radioButton = RadioButton(requireContext()).also { radioButton -> radioButton.text = it }
                backupRestoreFragmentRadioGroup.addView(radioButton, 0)
            }

            backupRestoreFragmentRestoreBackupButton.setOnClickListener {
                onRestoreBackupClick()
            }

            viewModel.radioChecked.observe(viewLifecycleOwner) {
                backupRestoreFragmentRestoreBackupButton.isEnabled = it != 0
            }
        }
    }

    private fun onRestoreBackupClick() {
        AlertDialog.Builder(requireContext())
                .setTitle("Please note")
                .setMessage("All existing data will be deleted. This cannot be undone.")
                .setPositiveButton("Ok, move on.") { _, _ -> createBackup() }
                .setNegativeButton("Cancel", null)
                .show()
    }

    private fun createBackup() {
        backupRestoreFragmentRestoreBackupButton.isEnabled = false
        for (i in 0 until backupRestoreFragmentRadioGroup.childCount) {
            backupRestoreFragmentRadioGroup.getChildAt(i).isEnabled = false
        }
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.restoreBackup(getBackupName(), requireContext())
            lifecycleScope.launch(Dispatchers.Main) {
                AlertDialog.Builder(requireContext())
                        .setCancelable(false)
                        .setTitle("Backup successfully restored")
                        .setPositiveButton("Great, thank you") { _, _ -> findNavController().popBackStack() }
                        .show()
            }
        }
    }

    private fun getBackupName(): String {
        return backupRestoreFragmentRadioGroup.findViewById<RadioButton>(backupRestoreFragmentRadioGroup.checkedRadioButtonId).text.toString()
    }
}