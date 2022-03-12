package com.ceaver.assin.alerts.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.alerts.Alert
import com.ceaver.assin.alerts.AlertRepository
import com.ceaver.assin.databinding.AlertListFragmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class AlertListFragment : Fragment() {

    private val alertListAdapter = AlertListAdapter(OnListItemClickListener())
    private lateinit var viewModel: AlertListViewModel
    private lateinit var binding: AlertListFragmentBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<AlertListViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = AlertListFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.alertListFragmentRecyclerView.adapter = alertListAdapter
        binding.alertListFragmentRecyclerView.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL))
        // binding.alertListFragmentAddAlertButton.setOnClickListener { findNavController().navigate(AlertListFragmentDirections.actionAlertListActivityToAlertInputFragment()) }

        viewModel.alerts.observe(viewLifecycleOwner) { alertListAdapter.submitList(it.sortedBy { alert -> alert.getListRowTitleText() }) }

        return binding.root
    }

    interface OnItemClickListener {
        fun onItemClick(item: Alert)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(alert: Alert) {
            // findNavController().navigate(AlertListFragmentDirections.actionAlertListActivityToAlertInputFragment(alert))
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.itemId == AlertListAdapter.MENU_ITEM_DELETE) {
            onDeleteItemClick()
        }
        return super.onContextItemSelected(item)
    }

    private fun onDeleteItemClick() {
        lifecycleScope.launch {
            val selectedAlert = alertListAdapter.currentLongClickAlert!!
            AlertRepository.delete(selectedAlert)
            Snackbar.make(binding.alertListFragmentCoordinatorLayout, "Action removed", Snackbar.LENGTH_LONG)
                    .setAction("Undo") { lifecycleScope.launch { AlertRepository.insert(selectedAlert) } }
                    .show()
        }
    }
}
