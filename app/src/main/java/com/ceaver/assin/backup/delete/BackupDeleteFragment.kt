package com.ceaver.assin.backup.delete

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
import com.ceaver.assin.databinding.BackupDeleteFragmentBinding
import kotlinx.android.synthetic.main.backup_delete_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BackupDeleteFragment : Fragment() {
    private lateinit var viewModel: BackupDeleteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<BackupDeleteViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: BackupDeleteFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.backup_delete_fragment, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.existingBackups.isEmpty()) {
            backupDeleteFragmentDeleteBackupButton.text = "No backups found"
        } else {
            viewModel.existingBackups.forEach {
                val radioButton = RadioButton(requireContext()).also { radioButton -> radioButton.text = it }
                backupDeleteFragmentRadioGroup.addView(radioButton, 0)
            }

            backupDeleteFragmentDeleteBackupButton.setOnClickListener {
                onDeleteBackupClick()
            }

            viewModel.radioChecked.observe(viewLifecycleOwner) {
                backupDeleteFragmentDeleteBackupButton.isEnabled = it != 0
            }
        }
    }

    private fun onDeleteBackupClick() {
        AlertDialog.Builder(requireContext())
                .setTitle("Please note")
                .setMessage("All data will be deleted. This cannot be undone.")
                .setPositiveButton("Ok, move on.") { _, _ -> createBackup() }
                .setNegativeButton("Cancel", null)
                .show()
    }

    private fun createBackup() {
        backupDeleteFragmentDeleteBackupButton.isEnabled = false
        for (i in 0 until backupDeleteFragmentRadioGroup.childCount) {
            backupDeleteFragmentRadioGroup.getChildAt(i).isEnabled = false
        }
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.deleteBackup(getBackupName(), requireContext())
            lifecycleScope.launch(Dispatchers.Main) {
                AlertDialog.Builder(requireContext())
                        .setCancelable(false)
                        .setTitle("Backup successfully deleted")
                        .setPositiveButton("Great, thank you") { _, _ -> findNavController().popBackStack() }
                        .show()
            }
        }
    }

    private fun getBackupName(): String {
        return backupDeleteFragmentRadioGroup.findViewById<RadioButton>(backupDeleteFragmentRadioGroup.checkedRadioButtonId).text.toString()
    }
}