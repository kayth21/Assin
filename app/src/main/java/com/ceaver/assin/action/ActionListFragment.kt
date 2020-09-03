package com.ceaver.assin.action

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_action_list.*
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class ActionListFragment : Fragment() {

    private val actionListAdapter = ActionListAdapter(OnListItemClickListener())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(com.ceaver.assin.R.layout.fragment_action_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        actionList.adapter = actionListAdapter
        actionList.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        createTradeButton.setOnClickListener {
            findNavController().navigate(ActionListFragmentDirections.actionActionListFragmentToActionInputFragment(ActionType.TRADE))
        }
        loadAllActions()
        swipeRefreshLayout.setOnRefreshListener { loadAllActions() }
    }

    private fun loadAllActions() {
        lifecycleScope.launch {
            val actions = ActionRepository.loadAllActions()
            onAllActionsLoaded(actions)
        }
    }

    private fun onAllActionsLoaded(actions: List<Action>) {
        actionListAdapter.actionList = actions.reversed(); actionListAdapter.notifyDataSetChanged(); swipeRefreshLayout?.isRefreshing = false
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        actionList.adapter = null
        actionList.removeItemDecorationAt(0)  // TODO Seriously?
        createTradeButton.setOnClickListener(null)
        swipeRefreshLayout.setOnRefreshListener(null)
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.DeleteAll) {
        loadAllActions()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.Delete) {
        loadAllActions()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.Insert) {
        loadAllActions()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.Update) {
        loadAllActions()
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