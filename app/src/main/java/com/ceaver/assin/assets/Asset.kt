package com.ceaver.assin.assets

import androidx.recyclerview.widget.DiffUtil
import com.ceaver.assin.markets.Title
import java.math.BigDecimal

data class Asset(//
        val title: Title,
        val amount: BigDecimal,
        val btcValue: BigDecimal,
        val usdValue: BigDecimal
) {
    object Difference : DiffUtil.ItemCallback<Asset>() {
        override fun areItemsTheSame(oldItem: Asset, newItem: Asset): Boolean {
            return oldItem.title.id == newItem.title.id
        }

        override fun areContentsTheSame(oldItem: Asset, newItem: Asset): Boolean {
            return oldItem.title.id == newItem.title.id
                    && oldItem.amount == newItem.amount
                    && oldItem.btcValue == newItem.btcValue
                    && oldItem.usdValue == newItem.usdValue
        }
    }
}
