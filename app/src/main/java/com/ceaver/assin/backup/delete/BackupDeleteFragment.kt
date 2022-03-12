package com.ceaver.assin.backup.delete

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ceaver.assin.databinding.BackupDeleteFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BackupDeleteFragment : Fragment() {
    private lateinit var viewModel: BackupDeleteViewModel
    private lateinit var binding: BackupDeleteFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<BackupDeleteViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = BackupDeleteFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (viewModel.existingBackups.isEmpty()) {
            binding.backupDeleteFragmentDeleteBackupButton.text = "No backups found"
        } else {
            viewModel.existingBackups.forEach {
                val radioButton = RadioButton(requireContext()).also { radioButton -> radioButton.text = it }
                binding.backupDeleteFragmentRadioGroup.addView(radioButton, 0)
            }

            binding.backupDeleteFragmentDeleteBackupButton.setOnClickListener {
                onDeleteBackupClick()
            }

            viewModel.radioChecked.observe(viewLifecycleOwner) {
                binding.backupDeleteFragmentDeleteBackupButton.isEnabled = it != 0
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
        binding.backupDeleteFragmentDeleteBackupButton.isEnabled = false
        for (i in 0 until binding.backupDeleteFragmentRadioGroup.childCount) {
            binding.backupDeleteFragmentRadioGroup.getChildAt(i).isEnabled = false
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
        return binding.backupDeleteFragmentRadioGroup.findViewById<RadioButton>(binding.backupDeleteFragmentRadioGroup.checkedRadioButtonId).text.toString()
    }
}