package com.ceaver.assin.markets

import android.arch.persistence.room.*

@Dao
interface TitleDao {

    @Query("select count(*) from title")
    fun countTitles(): Int

    @Query("select * from title order by rank")
    fun loadAllTitles(): List<Title>

    @Query("select * from title where category = 'CRYPTO' order by rank")
    fun loadAllCryptoTitles(): List<Title>

    @Query("select * from title where active >= 0 order by rank")
    fun loadActiveTitles(): List<Title>

    @Query("select * from title where active >= 0 and category = 'CRYPTO' order by rank")
    fun loadActiveCryptoTitles(): List<Title>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTitle(title: Title)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTitles(titles: Set<Title>)

    @Update
    fun updateTitle(title: Title)

    @Update
    fun updateTitles(titles: Set<Title>)

    @Delete
    fun deleteTitle(title: Title)

    @Delete
    fun deleteTitles(title: Set<Title>)

    @Query("delete from title")
    fun deleteAllTitles()

    @Query("select * from title where id = :id")
    fun loadTitle(id: String): Title

    @Query("select * from title where symbol = :symbol")
    fun loadTitleBySymbol(symbol: String): Title

    @Query("select symbol from title where category = 'CRYPTO' order by rank")
    fun loadCryptoSymbols(): List<String>

    @Query("select symbol from title where category = 'FIAT' order by rank")
    fun loadFiatSymbols(): List<String>
}