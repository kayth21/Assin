package com.ceaver.assin.positions.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.R
import com.ceaver.assin.positions.Position
import com.ceaver.assin.positions.PositionEvents
import com.ceaver.assin.positions.PositionRepository
import kotlinx.android.synthetic.main.position_list_fragment.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PositionListFragment : Fragment(){

    private val positionListAdapter = PositionListAdapter(OnListItemClickListener())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.position_list_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        positionListFragmentPositionList.adapter = positionListAdapter
        positionListFragmentPositionList.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL)) // TODO seriously?
        //positionListFragmentCreatePositionButton.setOnClickListener { PositionInputFragment().show(fragmentManager!!, PositionInputFragment.FRAGMENT_TAG) }
        loadAllPositions()
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        positionListFragmentPositionList.adapter = null
        positionListFragmentPositionList.removeItemDecorationAt(0) // TODO seriously?
        //positionListFragmentCreatePositionButton.setOnClickListener(null)
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: PositionEvents.Delete) {
        loadAllPositions()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: PositionEvents.Insert) {
        loadAllPositions()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: PositionEvents.Update) {
        loadAllPositions()
    }

    private fun loadAllPositions() {
        PositionRepository.loadAllPositionsAsync(true) { onAllPositionsLoaded(it) }
    }

    private fun onAllPositionsLoaded(positions: List<Position>) {
        positionListAdapter.positionList = positions
        positionListAdapter.notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(item: Position)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: Position) {
            // showDialogFragment(PositionInputFragment(), PositionInputFragment.FRAGMENT_TAG, PositionInputFragment.ADDRESS_ID, item.id)
        }
    }

    override fun onContextItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.groupId == PositionListAdapter.CONTEXT_MENU_GROUP_ID) {
            val selectedPosition = positionListAdapter.currentLongClickPosition!!
            when (menuItem.itemId) {
         //       PositionListAdapter.CONTEXT_MENU_EDIT_ITEM_ID -> showDialogFragment(PositionInputFragment(), PositionInputFragment.FRAGMENT_TAG, PositionInputFragment.ADDRESS_ID, selectedPosition.id)
        //        PositionListAdapter.CONTEXT_MENU_DELETE_ITEM_ID -> deletePosition(selectedPosition)
         //       PositionListAdapter.CONTEXT_MENU_RESET_ITEM_ID -> PositionRepository.updatePositionAsync(selectedPosition.copyForReset())
          //      PositionListAdapter.CONTEXT_MENU_SHOW_ITEM_ID -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://oxt.me/position/${selectedPosition.value}")));
                else -> throw IllegalStateException()
            }
        }
        return super.onContextItemSelected(menuItem)
    }

    private fun deletePosition(selectedPosition: Position) {
        //PositionRepository.deletePositionAsync(selectedPosition)
        //if (Preferences.isLoggingEnabled()) {
        //    LogRepository.insertLogAsync("Deleted position ${selectedPosition.value.take(20)}...", LogCategory.DELETE)
        //}
    }

    private fun showDialogFragment(dialogFragment: DialogFragment, fragmentTag: String, positionIdKey: String, positionIdValue: Long) {
       // val arguments = Bundle()
        //arguments.putLong(positionIdKey, positionIdValue)
       // dialogFragment.arguments = arguments
       // dialogFragment.show(fragmentManager!!, fragmentTag)
    }
}