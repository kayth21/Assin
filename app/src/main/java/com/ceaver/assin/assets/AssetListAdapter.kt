package com.ceaver.assin.assets

import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.ceaver.assin.MyApplication
import com.ceaver.assin.R
import com.ceaver.assin.extensions.format
import com.ceaver.assin.extensions.resIdByName

class AssetListAdapter : RecyclerView.Adapter<AssetListAdapter.ViewHolder>() {
    var assets: List<Asset> = ArrayList()
    var currentLongClickAsset: Asset? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.asset_list_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(assets[position])
        holder.itemView.setOnLongClickListener { currentLongClickAsset = assets[position]; false }
    }

    override fun getItemCount() = assets.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnCreateContextMenuListener {

        override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
            menu!!.add(0, 0, 0, "Deposit")
            menu.add(0, 1, 1, "Withdraw")
        }

        fun bindItem(asset: Asset) {
            (view.findViewById(R.id.assetImageView) as ImageView).setImageResource(getImageIdentifier(asset.title))
            (view.findViewById(R.id.assetNameTextView) as TextView).text = "${asset.amount} ${asset.title}"
            (view.findViewById(R.id.assetBtcValueTextView) as TextView).text = asset.btcValue.format(asset.title) + " " + "BTC"
            (view.findViewById(R.id.assetUsdValueTextView) as TextView).text = asset.usdValue.format(asset.title) + " " + "USD"
            view.setOnCreateContextMenuListener(this)
        }

        private fun getImageIdentifier(symbol: String): Int {
            val identifier = MyApplication.appContext!!.resIdByName(symbol.toLowerCase(), "drawable")
            return if (identifier == 0) R.drawable.unknown else identifier
        }
    }
}