package com.ceaver.assin.alerts.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.R
import com.ceaver.assin.alerts.Alert
import com.ceaver.assin.alerts.AlertRepository
import com.ceaver.assin.databinding.AlertListFragmentBinding
import kotlinx.android.synthetic.main.alert_list_fragment.*
import kotlinx.coroutines.launch

class AlertListFragment : Fragment() {

    private val alertListAdapter = AlertListAdapter(OnListItemClickListener())
    private lateinit var viewModel: AlertListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<AlertListViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: AlertListFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.alert_list_fragment, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.alertList.adapter = alertListAdapter

        viewModel.alerts.observe(viewLifecycleOwner, Observer { alertListAdapter.submitList(it) })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        alertList.addItemDecoration(DividerItemDecoration(requireActivity(), LinearLayoutManager.VERTICAL)) // TODO Seriously?
        createAlertButton.setOnClickListener { findNavController().navigate(AlertListFragmentDirections.actionAlertListActivityToAlertInputFragment()) }
    }

    override fun onStop() {
        super.onStop()
        alertList.removeItemDecorationAt(0)  // TODO Seriously?
        createAlertButton.setOnClickListener(null)
    }

    interface OnItemClickListener {
        fun onItemClick(item: Alert)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(alert: Alert) {
            findNavController().navigate(AlertListFragmentDirections.actionAlertListActivityToAlertInputFragment(alert))
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        lifecycleScope.launch {
            val selectedAlert = alertListAdapter.currentLongClickAlert!!
            AlertRepository.deleteAlert(selectedAlert)
        }
        return super.onContextItemSelected(item)
    }
}
