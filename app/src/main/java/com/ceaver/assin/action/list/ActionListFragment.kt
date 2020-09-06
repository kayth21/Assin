package com.ceaver.assin.action.list

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.R
import com.ceaver.assin.action.Action
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.action.ActionType
import com.ceaver.assin.databinding.ActionListFragmentBinding
import kotlinx.android.synthetic.main.action_list_fragment.*
import kotlinx.coroutines.launch


class ActionListFragment : Fragment() {

    private val actionListAdapter = ActionListAdapter(OnListItemClickListener())
    private lateinit var viewModel: ActionListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = viewModels<ActionListViewModel>().value
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: ActionListFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.action_list_fragment, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.actionList.adapter = actionListAdapter

        viewModel.titles.observe(viewLifecycleOwner, Observer { actionListAdapter.submitList(it.reversed()) })

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        actionList.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        createTradeButton.setOnClickListener {
            findNavController().navigate(ActionListFragmentDirections.actionActionListFragmentToActionInputFragment(ActionType.TRADE))
        }
    }

    override fun onStop() {
        super.onStop()
        actionList.removeItemDecorationAt(0)  // TODO Seriously?
        createTradeButton.setOnClickListener(null)
    }

    interface OnItemClickListener {
        fun onItemClick(item: Action)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: Action) {
            findNavController().navigate(ActionListFragmentDirections.actionActionListFragmentToActionInputFragment(item.getActionType(), item.toActionEntity()))
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.groupId == 3 && item.itemId == 0) {
            lifecycleScope.launch {
                val selectedTrade = actionListAdapter.currentLongClickAction!!
                // TODO Delete... could be tricky meanwhile actions are "linked" to positions. Maybe allow only last element to be deleted.
                ActionRepository.deleteAction(selectedTrade)
            }
        }
        return super.onContextItemSelected(item)
    }
}