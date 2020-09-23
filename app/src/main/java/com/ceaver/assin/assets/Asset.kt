package com.ceaver.assin.assets

import androidx.recyclerview.widget.DiffUtil
import com.ceaver.assin.markets.Title
import java.math.BigDecimal

data class Asset(//
        val title: Title,
        val quantity: BigDecimal,
        val valueCrypto: BigDecimal,
        val valueFiat: BigDecimal
) {
    object Difference : DiffUtil.ItemCallback<Asset>() {
        override fun areItemsTheSame(oldItem: Asset, newItem: Asset): Boolean {
            return oldItem.title.id == newItem.title.id
        }

        override fun areContentsTheSame(oldItem: Asset, newItem: Asset): Boolean {
            return oldItem.title.id == newItem.title.id
                    && oldItem.quantity == newItem.quantity
                    && oldItem.valueCrypto == newItem.valueCrypto
                    && oldItem.valueFiat == newItem.valueFiat
        }
    }
}
