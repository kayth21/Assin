package com.ceaver.assin.assets.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.R
import com.ceaver.assin.action.ActionEvents
import com.ceaver.assin.databinding.AssetOverviewFragmentBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AssetOverviewFragment : Fragment() {

    private lateinit var viewModel: AssetOverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<AssetOverviewViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: AssetOverviewFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.asset_overview_fragment, container, false)
        binding.assetOverviewViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        loadAssetOverview()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
    }

    private fun loadAssetOverview() {
        viewModel.loadAssetOverview()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Complete) {
        loadAssetOverview()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Observed) {
        loadAssetOverview()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.Insert) {
        loadAssetOverview()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.Update) {
        loadAssetOverview()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.Delete) {
        loadAssetOverview()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.DeleteAll) {
        loadAssetOverview()
    }
}
