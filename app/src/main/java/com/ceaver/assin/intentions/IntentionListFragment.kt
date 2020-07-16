package com.ceaver.assin.intentions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ceaver.assin.AssinWorkerEvents
import com.ceaver.assin.AssinWorkers
import com.ceaver.assin.R
import com.ceaver.assin.intentions.input.IntentionInputFragment
import com.ceaver.assin.threading.BackgroundThreadExecutor
import com.ceaver.assin.util.isConnected
import com.google.android.material.snackbar.Snackbar
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
        intentionListFragmentIntentionList.addItemDecoration(DividerItemDecoration(requireActivity().application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        intentionListFragmentCreateIntentionButton.setOnClickListener {
            val intentionInputFragment = IntentionInputFragment()
            intentionInputFragment.arguments = Bundle()
            intentionInputFragment.show(parentFragmentManager, IntentionInputFragment.INTENTION_INPUT_FRAGMENT_TAG)
        }
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

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Complete) {
        loadAllIntentions()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: AssinWorkerEvents.Observed) {
        loadAllIntentions()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: IntentionEvents.DeleteAll) {
        loadAllIntentions()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: IntentionEvents.Delete) {
        loadAllIntentions()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: IntentionEvents.Insert) {
        loadAllIntentions()
    }

    @Suppress("UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: IntentionEvents.Update) {
        loadAllIntentions()
    }

    private fun loadAllIntentions() {
        IntentionRepository.loadAllIntentionsAsync(true) { onAllIntentionsLoaded(it) }
    }

    private fun onAllIntentionsLoaded(intentions: List<Intention>) {
        intentionListAdapter.intentionList = intentions.toMutableList().sortedBy { it.percentToReferencePrice() }.reversed()
        intentionListAdapter.notifyDataSetChanged()
        intentionListFragmentSwipeRefreshLayout?.isRefreshing = false
    }

    interface OnItemClickListener {
        fun onItemClick(item: Intention)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: Intention) {
            val arguments = Bundle();
            arguments.putLong(IntentionInputFragment.INTENTION_ID, item.id)
            val intentionInputFragment = IntentionInputFragment()
            intentionInputFragment.arguments = arguments
            intentionInputFragment.show(parentFragmentManager, IntentionInputFragment.INTENTION_INPUT_FRAGMENT_TAG)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        if (item.groupId == 2 && item.itemId == 0) {
            val selectedIntention = intentionListAdapter.currentLongClickIntention!!
            IntentionRepository.deleteIntentionAsync(selectedIntention)
        }
        return super.onContextItemSelected(item)
    }
}
