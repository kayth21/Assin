package com.ceaver.assin.assets

import android.support.v7.widget.RecyclerView
import android.view.ContextMenu
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.ceaver.assin.R

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
            (view.findViewById(R.id.assetNameTextView) as TextView).text = asset.title
            (view.findViewById(R.id.assetAmountTextView) as TextView).text = asset.amount.toString()
            view.setOnCreateContextMenuListener(this)
        }
    }
}