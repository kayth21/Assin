package com.ceaver.assin.database

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

interface BaseEntityDao<T> {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: T)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg entities: T)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entities: Collection<T>)

    @Update
    suspend fun update(entity: T)

    @Update
    suspend fun update(vararg entities: T)

    @Update
    suspend fun update(entities: Collection<T>)

    @Delete
    suspend fun delete(entity: T)

    @Delete
    suspend fun delete(vararg entities: T)

    @Delete
    suspend fun delete(entities: Collection<T>)
}