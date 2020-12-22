package com.ceaver.assin.logging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.R
import com.ceaver.assin.databinding.LogListFragmentBinding

class LogListFragment : Fragment() {

    private val logListAdapter = LogListAdapter()
    private lateinit var viewModel: LogListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<LogListViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: LogListFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.log_list_fragment, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.logList.adapter = logListAdapter
        binding.logList.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL))

        viewModel.logs.observe(viewLifecycleOwner) { logListAdapter.submitList(it.sortedByDescending { it.id }) }

        return binding.root
    }
}
