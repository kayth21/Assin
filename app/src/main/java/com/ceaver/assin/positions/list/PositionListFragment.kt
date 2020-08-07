package com.ceaver.assin.positions.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.R
import com.ceaver.assin.action.Action
import com.ceaver.assin.action.ActionEvents
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.markets.Title
import com.ceaver.assin.positions.Position
import com.ceaver.assin.positions.PositionRepository
import kotlinx.android.synthetic.main.position_list_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PositionListFragment(val title: Title) : Fragment() {

    private val positionListAdapter = PositionListAdapter(OnListItemClickListener(), this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.position_list_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        positionListFragmentPositionList.adapter = positionListAdapter
        positionListFragmentPositionList.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL)) // TODO seriously?
        loadAllPositions()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        positionListFragmentPositionList.adapter = null
        positionListFragmentPositionList.removeItemDecorationAt(0) // TODO seriously?
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Complete) {
        loadAllPositions()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Observed) {
        loadAllPositions()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.Delete) {
        loadAllPositions()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.Insert) {
        loadAllPositions()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.Update) {
        loadAllPositions()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: ActionEvents.DeleteAll) {
        loadAllPositions()
    }

    private fun loadAllPositions() {
        PositionRepository.loadPositionsAsync(title, true) { onAllPositionsLoaded(it.sortedByDescending { it.id }) }
    }

    private fun onAllPositionsLoaded(positions: List<Position>) {
        positionListAdapter.positionList = positions
        positionListFragmentPositionList.adapter = positionListAdapter // instead of positionListAdapter.notifyDataSetChanged() to force context-menu reset
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
        ActionRepository.insertWithdrawAsync(Action.withdraw(selectedPosition), true) { loadAllPositions() }
    }
}