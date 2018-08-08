package com.ceaver.assin.advices

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ceaver.adviceadvisor.advices.AdviceRepository
import com.ceaver.assin.R
import com.ceaver.assin.trades.TradeEvents
import kotlinx.android.synthetic.main.fragment_advice_list.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AdviceListFragment : Fragment() {
    private val adviceListAdapter = com.ceaver.assin.advices.AdviceListAdapter(OnListItemClickListener())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_advice_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this);
        adviceList.adapter = adviceListAdapter
        adviceList.addItemDecoration(DividerItemDecoration(activity.application, LinearLayoutManager.VERTICAL)) // TODO Seriously?
        loadAllAdvices()
        swipeRefreshLayout.setOnRefreshListener { swipeRefreshLayout.isRefreshing = false; EventBus.getDefault().post(com.ceaver.assin.engine.EngineEvents.Run()) }
    }

    private fun loadAllAdvices() {
        AdviceRepository.loadAllAdvicesAsync(true) { onAllAdvicesLoaded(it) }
    }

    private fun onAllAdvicesLoaded(trades: List<com.ceaver.assin.advices.Advice>) {
        adviceListAdapter.adviceList = trades;
        adviceListAdapter.notifyDataSetChanged();
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this);
        adviceList.adapter = null
        swipeRefreshLayout.setOnRefreshListener(null)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: com.ceaver.assin.advices.AdviceEvents.Delete) {
        loadAllAdvices()
        Toast.makeText(getActivity(), "Advice deleted..", Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: TradeEvents.Delete) {
        loadAllAdvices()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: com.ceaver.assin.advices.AdviceEvents.Insert) {
        loadAllAdvices()
        Toast.makeText(getActivity(), "New Advice!", Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: com.ceaver.assin.advices.AdviceEvents.Update) {
        loadAllAdvices()
    }

    interface OnItemClickListener {
        fun onItemClick(item: com.ceaver.assin.advices.Advice)
    }

    private inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: com.ceaver.assin.advices.Advice) {
            val intent = Intent(activity.application, com.ceaver.assin.advices.AdviceInputActivity::class.java);
            intent.putExtra(com.ceaver.assin.IntentKeys.ADVICE_ID, item.id)
            startActivity(intent)
        }
    }
}
