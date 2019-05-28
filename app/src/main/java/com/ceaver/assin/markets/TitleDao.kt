package com.ceaver.assin.markets

import android.arch.persistence.room.*

@Dao
interface TitleDao {
    @Query("select * from title order by rank")
    fun loadAllTitles(): List<Title>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTitle(title: Title)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTitles(titles: Set<Title>)

    @Update
    fun updateTitle(title: Title)

    @Delete
    fun deleteTitle(title: Title)

    @Delete
    fun deleteTitles(title: Set<Title>)

    @Query("delete from title")
    fun deleteAllTitles()

    @Query("select * from title where id = :id")
    fun loadTitle(id: Long): Title

    @Query("select * from title where symbol = :symbol")
    fun loadTitleBySymbol(symbol: String): Title

    @Query("select symbol from title order by rank")
    fun loadCryptoSymbols(): List<String>
}