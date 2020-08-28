package com.ceaver.assin.action

import com.ceaver.assin.database.Database
import com.ceaver.assin.positions.Position
import com.ceaver.assin.positions.PositionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import java.math.BigDecimal

object ActionRepository {

    suspend fun loadAction(id: Long): Action = withContext(Dispatchers.IO) {
        return@withContext getActionDao().loadAction(id)
    }

    suspend fun loadAllActions(): List<Action> = withContext(Dispatchers.IO) {
        return@withContext getActionDao().loadAllActions()
    }

    suspend fun loadActions(symbol: String): List<Action> = withContext(Dispatchers.IO) {
        return@withContext getActionDao().loadAllActions().filter { it.buyTitle?.symbol == symbol || it.sellTitle?.symbol == symbol }
    }

    suspend fun insertDeposit(action: Action) = withContext(Dispatchers.IO) {
        insertAction(action)
    }

    // TODO Remove copy/paste from withdraw
    suspend fun insertTrade(action: Action) {
        withContext(Dispatchers.IO)
        {
            if (action.positionId != null) {
                insertAction(action)
            } else {
                val positions = PositionRepository.loadPositions(action.sellTitle!!).filter { it.isActive() }
                val oldestPosition = positions.first()
                when (oldestPosition.amount.compareTo(action.sellAmount)) {
                    0 -> {
                        insertTrade(action.copy(positionId = oldestPosition.id))
                    }
                    1 -> {
                        insertSplit(oldestPosition, action.sellAmount!!);
                        insertTrade(action)
                    }
                    -1 -> {
                        var index = 1
                        val mergeList = mutableListOf<Position>(oldestPosition, positions.get(1))
                        while (mergeList.map { it.amount }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.compareTo(action.sellAmount) == -1) {
                            index++
                            mergeList.add(positions.get(index))
                        }

                        when (mergeList.map { it.amount }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.compareTo(action.sellAmount)) {
                            0 -> {
                                mergeList.forEach {
                                    insertTrade(action.copy(positionId = it.id, sellAmount = it.amount))
                                }
                            }
                            1 -> {
                                val splitPosition = mergeList.last()
                                val splitAmount = mergeList.map { it.amount }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.minus(action.sellAmount!!)
                                insertSplit(splitPosition, splitAmount)
                                insertTrade(action)
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun insertWithdraw(action: Action) {
        withContext(Dispatchers.IO) {
            if (action.positionId != null) {
                insertAction(action)
            } else {
                val positions = PositionRepository.loadPositions(action.sellTitle!!).filter { it.isActive() }
                val oldestPosition = positions.first()
                when (oldestPosition.amount.compareTo(action.sellAmount)) {
                    0 -> {
                        insertWithdraw(action.copy(positionId = oldestPosition.id))
                    }
                    1 -> {
                        insertSplit(oldestPosition, action.sellAmount!!);
                        insertWithdraw(action)
                    }
                    -1 -> {
                        var index = 1
                        val mergeList = mutableListOf<Position>(oldestPosition, positions.get(1))
                        while (mergeList.map { it.amount }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.compareTo(action.sellAmount) == -1) {
                            index++
                            mergeList.add(positions.get(index))
                        }

                        when (mergeList.map { it.amount }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.compareTo(action.sellAmount)) {
                            0 -> {
                                mergeList.forEach {
                                    insertWithdraw(action.copy(positionId = it.id, sellAmount = it.amount))
                                }
                            }
                            1 -> {
                                val splitPosition = mergeList.last()
                                val overflowAmount = mergeList.map { it.amount }.reduce { acc, bigDecimal -> acc.add(bigDecimal) }.minus(action.sellAmount!!)
                                val splitAmount = splitPosition.amount.minus(overflowAmount)
                                insertSplit(splitPosition, splitAmount)
                                insertWithdraw(action)
                            }
                        }
                    }
                }
            }
        }
    }

    suspend fun insertSplit(position: Position, sellAmount: BigDecimal) = withContext(Dispatchers.IO) {
        insertAction(Action.split(position, sellAmount))
    }

    suspend fun insertActions(actions: List<Action>) = withContext(Dispatchers.IO) {
        getActionDao().insertActions(actions)
        EventBus.getDefault().post(ActionEvents.Insert())
    }

    suspend private fun insertAction(action: Action) = withContext(Dispatchers.IO) {
        getActionDao().insertAction(action)
        EventBus.getDefault().post(ActionEvents.Insert())
    }

    suspend fun updateAction(action: Action) = withContext(Dispatchers.IO) {
        getActionDao().updateAction(action); EventBus.getDefault().post(ActionEvents.Update())
    }

    suspend fun deleteAction(action: Action) = withContext(Dispatchers.IO) {
        getActionDao().deleteAction(action); EventBus.getDefault().post(ActionEvents.Delete())
    }

    suspend fun deleteAllActions() = withContext(Dispatchers.IO) {
        getActionDao().deleteAllActions(); EventBus.getDefault().post(ActionEvents.DeleteAll())
    }

    private fun getActionDao(): ActionDao {
        return getDatabase().actionDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}