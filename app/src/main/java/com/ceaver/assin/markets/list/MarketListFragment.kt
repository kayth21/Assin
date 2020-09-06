package com.ceaver.assin.markets.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.R
import com.ceaver.assin.databinding.MarketListFragmentBinding
import kotlinx.android.synthetic.main.market_list_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MarketListFragment : Fragment() {

    private lateinit var viewModel: MarketListViewModel
    private val marketListAdapter = MarketListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<MarketListViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: MarketListFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.market_list_fragment, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.marketList.adapter = marketListAdapter

        viewModel.titles.observe(viewLifecycleOwner, Observer { marketListAdapter.submitList(it) })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        marketList.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Complete) {
        Toast.makeText(activity, "Markets refreshed", Toast.LENGTH_SHORT).show();
        viewModel.loading.set(false)
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Observed) {
        Toast.makeText(activity, "Observed refreshed", Toast.LENGTH_SHORT).show();
        viewModel.loading.set(false)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
        marketList.removeItemDecorationAt(0) // TODO Seriously?
    }
}
