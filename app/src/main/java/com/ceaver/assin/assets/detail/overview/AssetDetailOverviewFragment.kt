package com.ceaver.assin.assets.detail.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ceaver.assin.R
import com.ceaver.assin.databinding.AssetDetailOverviewFragmentBinding
import com.ceaver.assin.markets.Title

class AssetDetailOverviewFragment(val title: Title, val label: String?) : Fragment() {

    private lateinit var viewModel: AssetDetailOverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<AssetDetailOverviewViewModel> { AssetDetailOverviewViewModel.Factory(title, label) }.value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: AssetDetailOverviewFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.asset_detail_overview_fragment, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}
