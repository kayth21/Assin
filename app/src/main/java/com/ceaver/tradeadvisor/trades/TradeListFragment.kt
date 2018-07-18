package com.ceaver.tradeadvisor.trades

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.ceaver.tradeadvisor.IntentKeys

import com.ceaver.tradeadvisor.R
import com.ceaver.tradeadvisor.trades.input.TradeInputActivity
import kotlinx.android.synthetic.main.active_trades_list.*

class TradeListFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_trade_list, container, false)
    }

    override fun onStart() {
        super.onStart()
        activeTradesList.adapter = TradeListAdapter(OnListItemClickListener())
        activeTradesList.addItemDecoration(DividerItemDecoration(getActivity().getApplication(), LinearLayoutManager.VERTICAL))

        createTradeButton.setOnClickListener(OnCreateTradeClickListener())
    }

    inner class OnListItemClickListener : OnItemClickListener {
        override fun onItemClick(item: Trade) {
            val intent = Intent(getActivity().getApplication(), TradeInputActivity::class.java);
            intent.putExtra(IntentKeys.TRADE_ID, item.id)
            startActivity(intent)
        }
    }

    inner class OnCreateTradeClickListener : View.OnClickListener {
        override fun onClick(v: View?) {
            val intent = Intent(getActivity().getApplication(), TradeInputActivity::class.java)
            startActivity(intent)
        }
    }

    public interface OnItemClickListener {
        fun onItemClick(item: Trade)
    }

    override fun onContextItemSelected(item: MenuItem?): Boolean {
        val selectedTrade = (activeTradesList.adapter as TradeListAdapter).currentLongClickTrade!!
        TradeRepository.deleteTrade(selectedTrade)
        (activeTradesList.adapter as TradeListAdapter).refresh()
        return super.onContextItemSelected(item)
    }
}
