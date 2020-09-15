package com.ceaver.assin.markets.list

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
import com.ceaver.assin.databinding.MarketListFragmentBinding
import com.ceaver.assin.markets.Title

class MarketListFragment : Fragment() {

    private lateinit var viewModel: MarketListViewModel
    private val marketListAdapter = MarketListAdapter(OnListItemClickListener())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<MarketListViewModel>().value
    }

    interface OnItemClickListener {
        fun onItemClick(item: Title)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: Title) {
            // TODO
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: MarketListFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.market_list_fragment, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.marketList.adapter = marketListAdapter
        binding.marketList.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL))

        viewModel.titles.observe(viewLifecycleOwner, Observer { marketListAdapter.submitList(it) })

        return binding.root
    }
}
