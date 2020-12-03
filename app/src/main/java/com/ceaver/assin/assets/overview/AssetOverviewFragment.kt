package com.ceaver.assin.assets.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.ceaver.assin.R
import com.ceaver.assin.databinding.AssetOverviewFragmentBinding

class AssetOverviewFragment : Fragment() {

    private lateinit var viewModel: AssetOverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<AssetOverviewViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding: AssetOverviewFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.asset_overview_fragment, container, false)
        binding.assetOverviewViewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }
}
