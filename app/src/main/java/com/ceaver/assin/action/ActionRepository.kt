package com.ceaver.assin.action

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.ceaver.assin.database.Database
import com.ceaver.assin.positions.Position
import com.ceaver.assin.positions.PositionFactory

object ActionRepository {

    suspend fun loadById(id: Long): Action =
            loadAll().single { it.id == id }

    suspend fun loadAll(): List<Action> {
        val actions = dao.loadAll().map { it.toAction() }
        val positions = PositionFactory.fromActions(actions, true)
        return actions.map { mapToActionWithPosition(it, positions) }
    }

    fun loadAllObserved(): LiveData<List<Action>> =
            Transformations.map(dao.loadAllObserved()) {
                val actions = it.map { it.toAction() }
                val positions = PositionFactory.fromActions(actions, true)
                actions.map { mapToActionWithPosition(it, positions) }
            }

    private fun mapToActionWithPosition(action: Action, positions: List<Position>): Action {
        return when (action.getActionType()) {
            ActionType.DEPOSIT -> action
            ActionType.WITHDRAW -> (action as Withdraw).copy(sourcePosition = positions.single { it.id == action.sourcePositionId })
            ActionType.TRADE -> (action as Trade).copy(sellPosition = positions.single { it.id == action.sellPositionId })
            ActionType.SPLIT -> (action as Split).copy(sourcePosition = positions.single { it.id == action.sourcePositionId })
            ActionType.MERGE -> (action as Merge).copy(sourcePositionA = positions.single { it.id == action.sourcePositionIdA }, sourcePositionB = positions.single { it.id == action.sourcePositionIdB })
            ActionType.MOVE -> (action as Move).copy(sourcePosition = positions.single { it.id == action.sourcePositionId })
        }
    }

    suspend fun insert(action: Action) : Long =
            action.toActionEntity().let { dao.insert(it) }

    suspend fun insertAll(actions: List<Action>) =
            actions.map { it.toActionEntity() }.let { dao.insert(it) }

    suspend fun update(action: Action) =
            action.toActionEntity().let { dao.update(it) }

    suspend fun delete(action: Action) =
            action.toActionEntity().let { dao.delete(it) }

    suspend fun deleteAll() =
            dao.deleteAll()

    suspend fun isLatest(selectedTrade: Action) =
            dao.loadAll().maxByOrNull { it.action.id }!!.action.id == selectedTrade.id

    private val dao: ActionEntityDao
        get() {
            return database.actionDao()
        }

    private val database: Database
        get() {
            return Database.getInstance()
        }
}