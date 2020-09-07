package com.ceaver.assin.positions.list

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
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.R
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.action.Withdraw
import com.ceaver.assin.databinding.PositionListFragmentBinding
import com.ceaver.assin.markets.Title
import com.ceaver.assin.positions.Position
import kotlinx.android.synthetic.main.position_list_fragment.*
import kotlinx.coroutines.launch

class PositionListFragment(val title: Title) : Fragment() {

    private val positionListAdapter = PositionListAdapter(OnListItemClickListener(), this)
    private lateinit var viewModel: PositionListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<PositionListViewModel> { PositionListViewModel.Factory(title) }.value
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: PositionListFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.position_list_fragment, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.positionListFragmentPositionList.adapter = positionListAdapter

        viewModel.positions.observe(viewLifecycleOwner, Observer { positionListAdapter.submitList(it.sortedByDescending { it.id }) })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        positionListFragmentPositionList.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL)) // TODO seriously?
    }

    override fun onStop() {
        super.onStop()
        positionListFragmentPositionList.removeItemDecorationAt(0) // TODO seriously?
    }

    interface OnItemClickListener {
        fun onItemClick(item: Position)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: Position) {
            // TODO
        }
    }

    override fun onContextItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.groupId == PositionListAdapter.CONTEXT_MENU_GROUP_ID) {
            val selectedPosition = positionListAdapter.currentLongClickPosition!!
            when (menuItem.itemId) {
                PositionListAdapter.CONTEXT_MENU_WITHDRAW_ITEM_ID -> withdraw(selectedPosition)
                else -> throw IllegalStateException()
            }
        }
        return super.onContextItemSelected(menuItem)
    }

    private fun withdraw(selectedPosition: Position) {
        lifecycleScope.launch {
            ActionRepository.insertWithdraw(Withdraw.fromPosition(selectedPosition))
        }
    }
}