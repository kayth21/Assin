package com.ceaver.assin.markets

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.ceaver.assin.database.BaseEntityDao

@Dao
interface TitleEntityDao : BaseEntityDao<TitleEntity> {
    @Query("select * from title where id = :id")
    suspend fun loadById(id: String): TitleEntity?

    @Query("select * from title where symbol = :symbol")
    suspend fun loadBySymbol(symbol: String): TitleEntity

    @Query("select * from title order by rank")
    suspend fun loadAll(): List<TitleEntity>

    @Query("select * from title where category = 'CRYPTO' order by rank")
    suspend fun loadAllCryptoTitles(): List<TitleEntity>

    @Query("select * from title where active >= 0 and category = 'CRYPTO' order by rank")
    fun loadAllActiveCryptoTitlesObserved(): LiveData<List<TitleEntity>>

    @Query("delete from title")
    suspend fun deleteAll()

    @Transaction
    suspend fun marketUpdate(titlesToInsert: List<TitleEntity>, titlesToUpdate: List<TitleEntity>, titlesToDelete: List<TitleEntity>) {
        insert(titlesToInsert)
        update(titlesToUpdate)
        delete(titlesToDelete)
    }
}