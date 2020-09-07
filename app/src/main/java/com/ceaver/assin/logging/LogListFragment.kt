package com.ceaver.assin.logging

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.R
import com.ceaver.assin.databinding.LogListFragmentBinding
import kotlinx.android.synthetic.main.log_list_fragment.*

class LogListFragment : Fragment() {

    private val logListAdapter = LogListAdapter()
    private lateinit var viewModel: LogListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<LogListViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: LogListFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.log_list_fragment, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.logList.adapter = logListAdapter

        viewModel.logs.observe(viewLifecycleOwner, Observer { logListAdapter.submitList(it.sortedByDescending { it.id }) })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        logList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)) // TODO Seriously?
    }

    override fun onStop() {
        super.onStop()
        logList.removeItemDecorationAt(0) // TODO Seriously?
    }

}
