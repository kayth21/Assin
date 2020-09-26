package com.ceaver.assin.positions

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.ceaver.assin.action.ActionRepository
import com.ceaver.assin.markets.Title

object PositionRepository {

    suspend fun loadPositions(title: Title): List<Position> =
            // Caution: Generate Positions always out of all actions and filter afterwards, because they need to be in line because of attribute positionId
            PositionFactory.fromActions(ActionRepository.loadAll()).filter { it.title == title }

    fun loadPositionsObserved(title: Title): LiveData<List<Position>> =
            // Caution: Generate Positions always out of all actions and filter afterwards, because they need to be in line because of attribute positionId
            Transformations.map(ActionRepository.loadAllObserved()) { PositionFactory.fromActions(it).filter { it.title == title } }

    suspend fun loadAllPositions(): List<Position> =
            PositionFactory.fromActions(ActionRepository.loadAll())

    fun loadAllPositionsObserved(): LiveData<List<Position>> =
            Transformations.map(ActionRepository.loadAllObserved()) { PositionFactory.fromActions(it) }
}