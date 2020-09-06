package com.ceaver.assin.action

import androidx.recyclerview.widget.DiffUtil
import java.time.LocalDate

interface Action {
    fun getEntityId(): Long
    fun getActionDate(): LocalDate
    fun getLeftImageResource(): Int
    fun getRightImageResource(): Int
    fun getTitleText(): String
    fun getDetailText(): String
    fun getActionType(): ActionType
    fun toActionEntity(): ActionEntity
    fun toExport(): List<String>


    object Difference : DiffUtil.ItemCallback<Action>() {
        override fun areItemsTheSame(oldItem: Action, newItem: Action): Boolean {
            return oldItem.getEntityId() == newItem.getEntityId()
        }

        override fun areContentsTheSame(oldItem: Action, newItem: Action): Boolean {
            return oldItem.equals(newItem)
        }
    }
}