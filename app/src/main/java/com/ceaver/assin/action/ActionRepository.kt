package com.ceaver.assin.action

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.ceaver.assin.database.Database
import com.ceaver.assin.markets.Title
import com.ceaver.assin.positions.Position
import com.ceaver.assin.positions.PositionRepository
import java.math.BigDecimal

object ActionRepository {

    suspend fun loadById(id: Long): Action =
            dao.loadById(id).toAction()

    suspend fun loadAll(): List<Action> =
            dao.loadAll().map { it.toAction() }

    fun loadAllObserved(): LiveData<List<Action>> =
            Transformations.map(dao.loadAllObserved()) { it.map { it.toAction() } }

    fun loadAllOfTitle(title: Title): List<Action> =
            dao.loadAllOfTitle(title.id).map { it.toAction() }

    fun loadAllOfTitleObserved(title: Title): LiveData<List<Action>> =
            Transformations.map(dao.loadAllOfTitleObserved(title.id)) { it.map { it.toAction() } }

    suspend fun insert(action: Action) =
            action.toActionEntity().let { dao.insert(it) }

    suspend fun insertActions(actions: List<Action>) =
            actions.map { it.toActionEntity() }.let { dao.insert(it) }

    suspend fun insertSplit(position: Position, sellQuantity: BigDecimal) =
            Split.fromPosition(position, sellQuantity).let { insert(it) }

    suspend fun insertMerge(positionA: Position, positionB: Position) =
            Merge.fromPositions(positionA, positionB).let { insert(it) }

    suspend fun update(action: Action) =
            action.toActionEntity().let { dao.update(it) }

    suspend fun delete(action: Action) =
            action.toActionEntity().let { dao.delete(it) }

    suspend fun deleteAll() =
            dao.deleteAll()

    // TODO Remove copy/paste from withdraw
    suspend fun insertTrade(trade: Trade) {
        if (trade.positionId != null) {
            insert(trade)
        } else {
            val positions = PositionRepository.loadByTitle(trade.sellTitle, trade.sellLabel).filter { it.isActive() }
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
            insert(withdraw)
        } else {
            val positions = PositionRepository.loadByTitle(withdraw.title, withdraw.label).filter { it.isActive() }
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

    private val dao: ActionEntityDao
        get() {
            return database.actionDao()
        }

    private val database: Database
        get() {
            return Database.getInstance()
        }
}