package com.ceaver.assin.assets.list

import android.view.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ceaver.assin.assets.Asset
import com.ceaver.assin.databinding.AssetListRowBinding
import com.ceaver.assin.extensions.asCurrencyString
import com.ceaver.assin.preferences.Preferences
import kotlin.random.Random

class AssetListAdapter(private val onClickListener: AssetListFragment.OnItemClickListener) : ListAdapter<Asset, AssetListAdapter.ViewHolder>(Difference) {

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

    class ViewHolder(val binding: AssetListRowBinding) : RecyclerView.ViewHolder(binding.root), View.OnCreateContextMenuListener {

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(Menu.NONE, MENU_ITEM_DEPOSIT, Menu.NONE, "Deposit")
            menu.add(Menu.NONE, MENU_ITEM_WITHDRAW, Menu.NONE, "Withdraw")
            menu.add(Menu.NONE, MENU_ITEM_INTENTION, Menu.NONE, "Intention")
        }

        fun bindItem(asset: Asset, onClickListener: AssetListFragment.OnItemClickListener) {
            binding.assetImageView.setImageResource(asset.title.getIcon())
            binding.assetNameTextView.text = "${asset.title.name} ${if (asset.label == null) "" else "(${asset.label})"}"
            binding.assetBalanceTextView.text = asset.quantity.asCurrencyString(asset.title)
            binding.assetUsdValueTextView.text = asset.current.valueFiat.asCurrencyString(Preferences.getFiatTitle())
            binding.assetBtcValueTextView.text = asset.current.valueCrypto.asCurrencyString(Preferences.getCryptoTitle())
            binding.asset1hChangeTextView.text = "1h: ${asset.title.getPercentChange1hString()}%"
            binding.asset24hChangeTextView.text = "24h: ${asset.title.getPercentChange24hString()}%"
            binding.asset7dChangeTextView.text = "7d: ${asset.title.getPercentChange7dString()}%"

            itemView.setOnCreateContextMenuListener(this)
            itemView.setOnClickListener { onClickListener.onItemClick(asset) }
        }
    }

    object Difference : DiffUtil.ItemCallback<Asset>() {
        override fun areItemsTheSame(oldItem: Asset, newItem: Asset): Boolean {
            return oldItem.title.id == newItem.title.id && oldItem.label == newItem.label
        }

        override fun areContentsTheSame(oldItem: Asset, newItem: Asset): Boolean {
            return oldItem.quantity == newItem.quantity
                    && oldItem.current.valueCrypto == newItem.current.valueCrypto
                    && oldItem.current.valueFiat == newItem.current.valueFiat
                    && oldItem.open.valueCrypto == newItem.open.valueCrypto
                    && oldItem.open.valueFiat == newItem.open.valueFiat
                    && oldItem.title.getPercentChange1hString() == newItem.title.getPercentChange1hString()
                    && oldItem.title.getPercentChange24hString() == newItem.title.getPercentChange24hString()
                    && oldItem.title.getPercentChange7dString() == newItem.title.getPercentChange7dString()
        }
    }

    companion object {
        val MENU_ITEM_DEPOSIT = Random.nextInt()
        val MENU_ITEM_WITHDRAW = Random.nextInt()
        val MENU_ITEM_INTENTION = Random.nextInt()
    }
}
