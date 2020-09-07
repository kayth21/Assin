package com.ceaver.assin.assets.list

import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.databinding.AssetListRowBinding
import com.ceaver.assin.extensions.toCurrencyString
import kotlin.random.Random

class AssetListAdapter(private val onClickListener: AssetListFragment.OnItemClickListener) : ListAdapter<Asset, AssetListAdapter.ViewHolder>(Asset.Difference) {

    companion object {
        val CONTEXT_MENU_GROUP_ID = Random.nextInt()
        val CONTEXT_MENU_DEPOSIT_ITEM_ID = Random.nextInt()
        val CONTEXT_MENU_WITHDRAW_ITEM_ID = Random.nextInt()
        val CONTEXT_MENU_INTENTION_ITEM_ID = Random.nextInt()
    }

    var currentLongClickAsset: Asset? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = AssetListRowBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(getItem(position), onClickListener)
        holder.itemView.setOnLongClickListener { currentLongClickAsset = getItem(holder.layoutPosition); false }
    }

    class ViewHolder(val binding: AssetListRowBinding) :  RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_DEPOSIT_ITEM_ID, 0, "Deposit")
            menu.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_WITHDRAW_ITEM_ID, 1, "Withdraw")
            menu.add(CONTEXT_MENU_GROUP_ID, CONTEXT_MENU_INTENTION_ITEM_ID, 2, "Intention")
        }

        fun bindItem(asset: Asset, onClickListener: AssetListFragment.OnItemClickListener) {
            binding.assetImageView.setImageResource(asset.title.getIcon())
            binding.assetNameTextView.text = asset.title.name
            binding.assetBalanceTextView.text = "${asset.amount} ${asset.title.symbol}"
            binding.assetBtcValueTextView.text = asset.btcValue.toCurrencyString(asset.title.symbol) + " " + "BTC"
            binding.assetUsdValueTextView.text = asset.usdValue.toCurrencyString(asset.title.symbol) + " " + "USD"
            binding.asset1hChangeTextView.text = "1h: ${asset.title.getPercentChange1hUsdString()}%"
            binding.asset24hChangeTextView.text = "24h: ${asset.title.getPercentChange24hUsdString()}%"
            binding.asset7dChangeTextView.text = "7d: ${asset.title.getPercentChange7dUsdString()}%"

            itemView.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(asset) }
        }
    }
}