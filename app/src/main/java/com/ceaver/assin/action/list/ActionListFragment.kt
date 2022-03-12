package com.ceaver.assin.action.list

import android.app.AlertDialog
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
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.action.Action
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.databinding.ActionListFragmentBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class ActionListFragment : Fragment() {

    private val actionListAdapter = ActionListAdapter(OnListItemClickListener())
    private lateinit var viewModel: ActionListViewModel
    private lateinit var binding: ActionListFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<ActionListViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ActionListFragmentBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.actionListFragmentRecyclerView.adapter = actionListAdapter
        binding.actionListFragmentRecyclerView.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL))
        // binding.actionListFragmentAddActionFloatingButton.setOnClickListener { findNavController().navigate(ActionListFragmentDirections.actionActionListFragmentToActionInputFragment(ActionType.TRADE)) }

        viewModel.titles.observe(viewLifecycleOwner) { actionListAdapter.submitList(it.reversed()) }

        actionListAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding.actionListFragmentRecyclerView.scrollToPosition(0) // to scroll to the top on undo action
            }
        })

        return binding.root
    }

    interface OnItemClickListener {
        fun onItemClick(item: Action)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: Action) {
            // findNavController().navigate(ActionListFragmentDirections.actionActionListFragmentToActionInputFragment(item.getActionType(), item.toActionEntity()))
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            ActionListAdapter.MENU_ITEM_DELETE -> onDeleteItemClick()
        }
        return super.onContextItemSelected(item)
    }

    private fun onDeleteItemClick() {
        lifecycleScope.launch {
            val selectedTrade = actionListAdapter.currentLongClickAction!!
            if (ActionRepository.isLatest(selectedTrade)) {
                ActionRepository.delete(selectedTrade)
                Snackbar.make(binding.actionListFragmentCoordinatorLayout, "Action removed", Snackbar.LENGTH_LONG)
                        .setAction("Undo") { lifecycleScope.launch { ActionRepository.insert(selectedTrade) } }
                        .show()
            } else
                AlertDialog.Builder(requireContext())
                        .setTitle("Cannot remove this action")
                        .setMessage("Actions are stacked on top of each other, that's why it is always only possible to remove the top action.")
                        .setPositiveButton("Got it", null)
                        .show()
        }
    }
}