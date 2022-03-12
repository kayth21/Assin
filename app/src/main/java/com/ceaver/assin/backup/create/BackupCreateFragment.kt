package com.ceaver.assin.backup.create

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ceaver.assin.R
import com.ceaver.assin.databinding.BackupCreateFragmentBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class BackupCreateFragment : Fragment() {
    private lateinit var viewModel: BackupCreateViewModel
    private lateinit var binding: BackupCreateFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<BackupCreateViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding  = BackupCreateFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.existingBackups.forEach {
            val radioButton = RadioButton(requireContext()).also { radioButton -> radioButton.text = it }
            binding.backupCreateFragmentRadioGroup.addView(radioButton, 0)
        }

        binding.backupCreateFragmentCreateBackupButton.setOnClickListener {
            onCreateBackupClick()
        }

        viewModel.radioChecked.observe(viewLifecycleOwner) {
            binding.backupCreateFragmentNewBackupEditText.isEnabled = it == R.id.backupCreateFragmenNewBackupRadioButton
            binding.backupCreateFragmentCreateBackupButton.isEnabled = it != 0 && (it != R.id.backupCreateFragmenNewBackupRadioButton || binding.backupCreateFragmentNewBackupEditText.text.isNotEmpty())
        }

        binding.backupCreateFragmentNewBackupEditText.addTextChangedListener {
            binding.backupCreateFragmentCreateBackupButton.isEnabled = binding.backupCreateFragmentNewBackupEditText.text.isNotEmpty()
        }
    }

    private fun onCreateBackupClick() {
        hideKeyboard()

        if (viewModel.existingBackups.contains(getBackupName().toUpperCase(Locale.ROOT))) {
            AlertDialog.Builder(requireContext())
                    .setTitle("Please note")
                    .setMessage("Existing backup files will be overwritten. This cannot be undone.")
                    .setPositiveButton("OK, move on") { _, _ -> createBackup() }
                    .setNegativeButton("Cancel", null)
                    .show()
        } else {
            createBackup()
        }
    }

    private fun createBackup() {
        binding.backupCreateFragmentCreateBackupButton.isEnabled = false
        for (i in 0 until binding.backupCreateFragmentRadioGroup.childCount) {
            binding.backupCreateFragmentRadioGroup.getChildAt(i).isEnabled = false
        }
        lifecycleScope.launch(Dispatchers.IO) {
            val result = viewModel.createBackup(getBackupName(), requireContext())
            lifecycleScope.launch(Dispatchers.Main) {
                AlertDialog.Builder(requireContext())
                        .setCancelable(false)
                        .setTitle("Backup successfully created")
                        .setMessage(result)
                        .setPositiveButton("Great, thank you") { _, _ -> findNavController().popBackStack() }
                        .show()
            }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (requireActivity().currentFocus != null)
            inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, 0)
    }

    private fun getBackupName(): String {
        return when (binding.backupCreateFragmentRadioGroup.checkedRadioButtonId) {
            binding.backupCreateFragmenNewBackupRadioButton.id -> binding.backupCreateFragmentNewBackupEditText.text.toString().toUpperCase(Locale.ROOT)
            else -> {
                binding.backupCreateFragmentRadioGroup.findViewById<RadioButton>(binding.backupCreateFragmentRadioGroup.checkedRadioButtonId).text.toString()
            }
        }
    }
}
