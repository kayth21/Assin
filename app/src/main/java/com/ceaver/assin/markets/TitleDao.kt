package com.ceaver.assin.markets

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TitleDao {

    @Query("select * from title order by rank")
    suspend fun loadAllTitles(): List<TitleEntity>

    @Query("select * from title where category = 'CRYPTO' order by rank")
    suspend fun loadAllCryptoTitles(): List<TitleEntity>

    @Query("select * from title where active >= 0 order by rank")
    suspend fun loadActiveTitles(): List<TitleEntity>

    @Query("select * from title where active >= 0 and category = 'CRYPTO' order by rank")
    fun loadActiveCryptoTitles(): LiveData<List<TitleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTitle(title: TitleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTitles(titles: List<TitleEntity>)

    @Update
    suspend fun updateTitle(title: TitleEntity)

    @Update
    suspend fun updateTitles(titles: List<TitleEntity>)

    @Delete
    suspend fun deleteTitle(title: TitleEntity)

    @Delete
    suspend fun deleteTitles(title: List<TitleEntity>)

    @Query("delete from title")
    suspend fun deleteAllTitles()

    @Query("select * from title where id = :id")
    suspend fun loadTitle(id: String): TitleEntity?

    @Query("select * from title where id = :id")
    fun loadTitleReg(id: String): TitleEntity? // TODO only used because of TypeConverter cannot deal with suspend function, may find a better solution

    @Query("select * from title where symbol = :symbol")
    suspend fun loadTitleBySymbol(symbol: String): TitleEntity

    @Query("select symbol from title where category = 'CRYPTO' order by rank")
    suspend fun loadCryptoSymbols(): List<String>

    @Query("select symbol from title where category = 'FIAT' order by rank")
    suspend fun loadFiatSymbols(): List<String>

    @Transaction
    suspend fun marketUpdate(titlesToInsert: List<TitleEntity>, titlesToUpdate: List<TitleEntity>, titlesToDelete: List<TitleEntity>) {
        insertTitles(titlesToInsert)
        updateTitles(titlesToUpdate)
        deleteTitles(titlesToDelete)
    }
}