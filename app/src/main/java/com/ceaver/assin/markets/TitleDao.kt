package com.ceaver.assin.markets

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TitleDao {

    @Query("select * from title order by rank")
    suspend fun loadAllTitles(): List<Title>

    @Query("select * from title where category = 'CRYPTO' order by rank")
    suspend fun loadAllCryptoTitles(): List<Title>

    @Query("select * from title where active >= 0 order by rank")
    suspend fun loadActiveTitles(): List<Title>

    @Query("select * from title where active >= 0 and category = 'CRYPTO' order by rank")
    fun loadActiveCryptoTitles(): LiveData<List<Title>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTitle(title: Title)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTitles(titles: Set<Title>)

    @Update
    suspend fun updateTitle(title: Title)

    @Update
    suspend fun updateTitles(titles: Set<Title>)

    @Delete
    suspend fun deleteTitle(title: Title)

    @Delete
    suspend fun deleteTitles(title: Set<Title>)

    @Query("delete from title")
    suspend fun deleteAllTitles()

    @Query("select * from title where id = :id")
    suspend fun loadTitle(id: String): Title?

    @Query("select * from title where id = :id")
    fun loadTitleReg(id: String): Title? // TODO only used because of TypeConverter cannot deal with suspend function, may find a better solution

    @Query("select * from title where symbol = :symbol")
    suspend fun loadTitleBySymbol(symbol: String): Title

    @Query("select symbol from title where category = 'CRYPTO' order by rank")
    suspend fun loadCryptoSymbols(): List<String>

    @Query("select symbol from title where category = 'FIAT' order by rank")
    suspend fun loadFiatSymbols(): List<String>

    @Transaction
    suspend fun marketUpdate(titlesToInsert: Set<Title>, titlesToUpdate: Set<Title>, titlesToDelete: Set<Title>) {
        insertTitles(titlesToInsert)
        updateTitles(titlesToUpdate)
        deleteTitles(titlesToDelete)
    }
}