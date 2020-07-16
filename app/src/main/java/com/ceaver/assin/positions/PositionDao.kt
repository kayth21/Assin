package com.ceaver.assin.positions

import androidx.room.*

@Dao
interface PositionDao {
    @Query("select * from position")
    fun loadAllPositions(): List<Position>

    @Query("select * from position where id = :id")
    fun loadPosition(id: Long): Position

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertPosition(position: Position) : Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insertPositions(positions: List<Position>): List<Long>

    @Update
    fun updatePosition(position: Position)

    @Update
    fun updatePositions(positions: List<Position>)

    @Delete
    fun deletePosition(position: Position)

    @Query("delete from position")
    fun deleteAllPositions()
}