package com.ceaver.assin.assets.list

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.R
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.extensions.toCurrencyString
import kotlin.random.Random

class AssetListAdapter(private val onClickListener: AssetListFragment.OnItemClickListener) : RecyclerView.Adapter<AssetListAdapter.ViewHolder>() {

    companion object {
        val CONTEXT_MENU_GROUP_ID = Random.nextInt()
        val CONTEXT_MENU_DEPOSIT_ITEM_ID = Random.nextInt()
        val CONTEXT_MENU_WITHDRAW_ITEM_ID = Random.nextInt()
        val CONTEXT_MENU_INTENTION_ITEM_ID = Random.nextInt()
    }

    var assets: List<Asset> = ArrayList()
    var currentLongClickAsset: Asset? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.asset_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(assets[position], onClickListener)
        holder.itemView.setOnLongClickListener { currentLongClickAsset = assets[position]; false }
    }

    override fun getItemCount() = assets.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_DEPOSIT_ITEM_ID, 0, "Deposit")
            menu.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_WITHDRAW_ITEM_ID, 1, "Withdraw")
            menu.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_INTENTION_ITEM_ID, 2, "Intention")
        }

        fun bindItem(asset: Asset, onClickListener: AssetListFragment.OnItemClickListener) {
            (view.findViewById(R.id.assetImageView) as ImageView).setImageResource(asset.title.getIcon())
            (view.findViewById(R.id.assetNameTextView) as TextView).text = asset.title.name
            (view.findViewById(R.id.assetBalanceTextView) as TextView).text = "${asset.amount} ${asset.title.symbol}"
            (view.findViewById(R.id.assetBtcValueTextView) as TextView).text = asset.btcValue.toCurrencyString(asset.title.symbol) + " " + "BTC"
            (view.findViewById(R.id.assetUsdValueTextView) as TextView).text = asset.usdValue.toCurrencyString(asset.title.symbol) + " " + "USD"
            (view.findViewById(R.id.asset1hChangeTextView) as TextView).text = "1h: ${asset.title.getPercentChange1hUsdString()}%"
            (view.findViewById(R.id.asset24hChangeTextView) as TextView).text = "24h: ${asset.title.getPercentChange24hUsdString()}%"
            (view.findViewById(R.id.asset7dChangeTextView) as TextView).text = "7d: ${asset.title.getPercentChange7dUsdString()}%"

            view.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(asset) }
        }
    }
}