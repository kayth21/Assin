package com.ceaver.assin.action

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.ceaver.assin.database.Database
import com.ceaver.assin.positions.Position
import com.ceaver.assin.positions.PositionRepository
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal

object ActionRepository {

    suspend fun loadAction(id: Long): Action {
        return getActionDao().loadActionDto(id).toAction()
    }

    fun loadAllActionsObserved(): LiveData<List<Action>> {
        return Transformations.map(getActionDao().getActionDtosObserved()) { it.map { it.toAction() } }
    }

    suspend fun loadAllActions(): List<Action> {
        return getActionDao().getActionDtos().map { it.toAction() }
    }

    suspend fun insertDeposit(deposit: Deposit) {
        insertAction(deposit)
    }

    // TODO Remove copy/paste from withdraw
    suspend fun insertTrade(trade: Trade) {
        if (trade.positionId != null) {
            insertAction(trade)
        } else {
            val positions = PositionRepository.loadPositions(trade.sellTitle).filter { it.isActive() }
            val oldestPosition = positions.first()
            when (oldestPosition.quantity.compareTo(trade.sellQuantity)) {
                0 -> {
                    insertTrade(trade.copy(positionId = oldestPosition.id))
                }
                1 -> {
                    insertSplit(oldestPosition, trade.sellQuantity);
                    insertTrade(trade)
                }
                -1 -> {
                    var index = 1
                    val mergeList = mutableListOf(oldestPosition, positions.get(1))
                    while (mergeList.map { it.quantity }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.compareTo(trade.sellQuantity) == -1) {
                        index++
                        mergeList.add(positions.get(index))
                    }

                    when (mergeList.map { it.quantity }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.compareTo(trade.sellQuantity)) {
                        0 -> {
                            mergeList.forEach {
                                insertTrade(trade.copy(positionId = it.id, sellQuantity = it.quantity))
                            }
                        }
                        1 -> {
                            val splitPosition = mergeList.last()
                            val splitQuantity = mergeList.map { it.quantity }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.minus(trade.sellQuantity)
                            insertSplit(splitPosition, splitQuantity)
                            insertTrade(trade)
                        }
                    }
                }
            }
        }
    }

    suspend fun insertWithdraw(withdraw: Withdraw) {
        if (withdraw.positionId != null) {
            insertAction(withdraw)
        } else {
            val positions = PositionRepository.loadPositions(withdraw.title).filter { it.isActive() }
            val oldestPosition = positions.first()
            when (oldestPosition.quantity.compareTo(withdraw.quantity)) {
                0 -> {
                    insertWithdraw(withdraw.copy(positionId = oldestPosition.id))
                }
                1 -> {
                    insertSplit(oldestPosition, withdraw.quantity);
                    insertWithdraw(withdraw)
                }
                -1 -> {
                    var index = 1
                    val mergeList = mutableListOf<Position>(oldestPosition, positions.get(1))
                    while (mergeList.map { it.quantity }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.compareTo(withdraw.quantity) == -1) {
                        index++
                        mergeList.add(positions.get(index))
                    }

                    when (mergeList.map { it.quantity }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.compareTo(withdraw.quantity)) {
                        0 -> {
                            mergeList.forEach {
                                insertWithdraw(withdraw.copy(positionId = it.id, quantity = it.quantity))
                            }
                        }
                        1 -> {
                            val splitPosition = mergeList.last()
                            val overflowQuantity = mergeList.map { it.quantity }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.minus(withdraw.quantity)
                            val splitQuantity = splitPosition.quantity.minus(overflowQuantity)
                            insertSplit(splitPosition, splitQuantity)
                            insertWithdraw(withdraw)
                        }
                    }
                }
            }
        }
    }

    suspend fun insertSplit(position: Position, sellQuantity: BigDecimal) {
        insertAction(Split.fromPosition(position, sellQuantity))
    }

    suspend fun insertActions(actions: List<Action>) {
        getActionDao().insertActionEntities(actions.map { it.toActionEntity() })
        EventBus.getDefault().post(ActionEvents.Insert())
    }

    private suspend fun insertAction(action: Action) {
        getActionDao().insertActionEntity(action.toActionEntity())
        EventBus.getDefault().post(ActionEvents.Insert())
    }

    suspend fun updateAction(action: Action) {
        getActionDao().updateActionEntity(action.toActionEntity()); EventBus.getDefault().post(ActionEvents.Update())
    }

    suspend fun deleteAction(action: Action) {
        getActionDao().deleteActionEntity(action.toActionEntity()); EventBus.getDefault().post(ActionEvents.Delete())
    }

    suspend fun deleteAllActions() {
        getActionDao().deleteAllActionEntities(); EventBus.getDefault().post(ActionEvents.DeleteAll())
    }

    private fun getActionDao(): ActionEntityDao {
        return getDatabase().actionDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}