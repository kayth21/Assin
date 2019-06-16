package com.ceaver.assin.intentions

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.AssinWorkers
import com.ceaver.assin.R
import com.ceaver.assin.threading.BackgroundThreadExecutor
import com.ceaver.assin.util.isConnected
import kotlinx.android.synthetic.main.fragment_asset_list.marketFrameLayout
import kotlinx.android.synthetic.main.fragment_intention_list.*
import kotlinx.android.synthetic.main.fragment_market_list.*
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
        intentionListFragmentSwipeRefreshLayout.setOnRefreshListener {
            if (isConnected())
                BackgroundThreadExecutor.execute { AssinWorkers.observedUpdate() }
            else {
                Snackbar.make(marketFrameLayout, "no internet connection", Snackbar.LENGTH_LONG).show(); marketSwipeRefreshLayout.isRefreshing = false
            }
        }
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        intentionListFragmentIntentionList.adapter = null
        intentionListFragmentIntentionList.removeItemDecorationAt(0) // TODO Seriously?
        intentionListFragmentCreateIntentionButton.setOnClickListener(null)
        intentionListFragmentSwipeRefreshLayout.setOnRefreshListener(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Complete) {
        loadAllIntentions()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Observed) {
        loadAllIntentions()
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
            intent.putExtra(IntentionInputActivity.INTENT_EXTRA_INTENTION_ID, intention.id)
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
