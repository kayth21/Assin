package com.ceaver.assin.positions.list

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
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.action.Withdraw
import com.ceaver.assin.databinding.PositionListFragmentBinding
import com.ceaver.assin.markets.Title
import com.ceaver.assin.positions.Position
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class PositionListFragment(val title: Title, val label: String?) : Fragment() {

    private val positionListAdapter = PositionListAdapter(OnListItemClickListener(), this)
    private lateinit var viewModel: PositionListViewModel
    private lateinit var binding: PositionListFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<PositionListViewModel> { PositionListViewModel.Factory(title, label) }.value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = PositionListFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.positionListFragmentPositionList.adapter = positionListAdapter
        binding.positionListFragmentPositionList.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL))

        viewModel.positions.observe(viewLifecycleOwner) { positionListAdapter.submitList(it.sortedByDescending { it.id }) }

        return binding.root
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
        when (menuItem.itemId) {
            PositionListAdapter.MENU_ITEM_WITHDRAW -> onWithdrawClick()
        }
        return super.onContextItemSelected(menuItem)
    }

    private fun onWithdrawClick() {
        val selectedPosition = positionListAdapter.currentLongClickPosition!!
        lifecycleScope.launch {
            val withdrawId = ActionRepository.insert(Withdraw.fromPosition(selectedPosition))
            Snackbar.make(binding.positionListFragmentCoordinatorLayout, "Position closed", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        lifecycleScope.launch {
                            val withdrawAction = ActionRepository.loadById(withdrawId)
                            if (ActionRepository.isLatest(withdrawAction))
                                ActionRepository.delete(withdrawAction)
                        }
                    }
                    .show()
        }
    }
}