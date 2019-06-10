package com.ceaver.assin.intentions

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.ceaver.assin.R
import kotlinx.android.synthetic.main.fragment_intention_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class IntentionListFragment : Fragment() {

    private val intentionListAdapter = IntentionListAdapter(OnListItemClickListener())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intention_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        intentionListFragmentIntentionList.adapter = intentionListAdapter
        intentionListFragmentIntentionList.addItemDecoration(DividerItemDecoration(activity!!.application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        intentionListFragmentCreateIntentionButton.setOnClickListener { startActivity(Intent(activity!!.application, IntentionInputActivity::class.java)) }
        loadAllIntentions()
        intentionListFragmentSwipeRefreshLayout.setOnRefreshListener { loadAllIntentions() }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        intentionListFragmentIntentionList.adapter = null
        intentionListFragmentCreateIntentionButton.setOnClickListener(null)
        intentionListFragmentSwipeRefreshLayout.setOnRefreshListener(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: IntentionEvents.Delete) {
        loadAllIntentions()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: IntentionEvents.Insert) {
        loadAllIntentions()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: IntentionEvents.Update) {
        loadAllIntentions()
    }

    private fun loadAllIntentions() {
        IntentionRepository.loadAllIntentionsAsync(true) { onAllIntentionsLoaded(it) }
    }

    private fun onAllIntentionsLoaded(intentions: List<Intention>) {
        intentionListAdapter.intentionList = intentions
        intentionListAdapter.notifyDataSetChanged()
        intentionListFragmentSwipeRefreshLayout.isRefreshing = false
    }

    interface OnItemClickListener {
        fun onItemClick(item: Intention)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(intention: Intention) {
            val intent = Intent(activity!!.application, IntentionInputActivity::class.java);
            intent.putExtra(com.ceaver.assin.IntentKeys.INTENTION_ID, intention.id)
            startActivity(intent)
        }
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        if (item!!.groupId == 2 && item.itemId == 0) {
            val selectedIntention = intentionListAdapter.currentLongClickIntention!!
            IntentionRepository.deleteIntentionAsync(selectedIntention)
        }
        return super.onContextItemSelected(item)
    }
}
