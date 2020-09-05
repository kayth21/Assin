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
        return getActionDao().loadActionEntity(id).toAction()
    }

    fun loadAllActionsObserved(): LiveData<List<Action>> {
        return Transformations.map(getActionDao().loadAllActionEntitiesObserved()) { it.map { it.toAction() } }
    }

    suspend fun loadAllActions(): List<Action> {
        return getActionDao().loadAllActionEntities().map { it.toAction() }
    }

    suspend fun loadActions(symbol: String): List<Action> {
        return getActionDao().loadAllActionEntities().filter { it.buyTitle?.symbol == symbol || it.sellTitle?.symbol == symbol }.map { it.toAction() }
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
            when (oldestPosition.amount.compareTo(trade.sellAmount)) {
                0 -> {
                    insertTrade(trade.copy(positionId = oldestPosition.id))
                }
                1 -> {
                    insertSplit(oldestPosition, trade.sellAmount);
                    insertTrade(trade)
                }
                -1 -> {
                    var index = 1
                    val mergeList = mutableListOf(oldestPosition, positions.get(1))
                    while (mergeList.map { it.amount }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.compareTo(trade.sellAmount) == -1) {
                        index++
                        mergeList.add(positions.get(index))
                    }

                    when (mergeList.map { it.amount }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.compareTo(trade.sellAmount)) {
                        0 -> {
                            mergeList.forEach {
                                insertTrade(trade.copy(positionId = it.id, sellAmount = it.amount))
                            }
                        }
                        1 -> {
                            val splitPosition = mergeList.last()
                            val splitAmount = mergeList.map { it.amount }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.minus(trade.sellAmount)
                            insertSplit(splitPosition, splitAmount)
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
            when (oldestPosition.amount.compareTo(withdraw.amount)) {
                0 -> {
                    insertWithdraw(withdraw.copy(positionId = oldestPosition.id))
                }
                1 -> {
                    insertSplit(oldestPosition, withdraw.amount);
                    insertWithdraw(withdraw)
                }
                -1 -> {
                    var index = 1
                    val mergeList = mutableListOf<Position>(oldestPosition, positions.get(1))
                    while (mergeList.map { it.amount }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.compareTo(withdraw.amount) == -1) {
                        index++
                        mergeList.add(positions.get(index))
                    }

                    when (mergeList.map { it.amount }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.compareTo(withdraw.amount)) {
                        0 -> {
                            mergeList.forEach {
                                insertWithdraw(withdraw.copy(positionId = it.id, amount = it.amount))
                            }
                        }
                        1 -> {
                            val splitPosition = mergeList.last()
                            val overflowAmount = mergeList.map { it.amount }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.minus(withdraw.amount)
                            val splitAmount = splitPosition.amount.minus(overflowAmount)
                            insertSplit(splitPosition, splitAmount)
                            insertWithdraw(withdraw)
                        }
                    }
                }
            }
        }
    }

    suspend fun insertSplit(position: Position, sellAmount: BigDecimal) {
        insertAction(Split.fromPosition(position, sellAmount))
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