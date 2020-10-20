package com.ceaver.assin.action

import androidx.recyclerview.widget.DiffUtil
import java.time.LocalDate

interface Action {
    val id: Long
    val date: LocalDate
    val comment: String?
    fun getLeftImageResource(): Int
    fun getRightImageResource(): Int
    fun getTitleText(): String
    fun getDetailText(): String
    fun getActionType(): ActionType
    fun toActionEntity(): ActionEntity
    fun toExport(): List<String>


    object Difference : DiffUtil.ItemCallback<Action>() {
        override fun areItemsTheSame(oldItem: Action, newItem: Action): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Action, newItem: Action): Boolean {
            return oldItem.equals(newItem)
        }
    }
}