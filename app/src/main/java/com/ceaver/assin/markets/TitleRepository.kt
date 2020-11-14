package com.ceaver.assin.markets

import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.ceaver.assin.database.Database

object TitleRepository {

    suspend fun loadById(id: String): Title {
        return dao.loadById(id).toTitle()
    }

    suspend fun loadBySymbol(symbol: String): Title {
        return dao.loadBySymbol(symbol).toTitle();
    }

    suspend fun loadAll(): List<Title> {
        return dao.loadAll().map { it.toTitle() }
    }

    suspend fun loadAllCryptoTitles(): List<CryptoTitle> {
        return dao.loadAllCryptoTitles().map { it.toTitle() as CryptoTitle }
    }

    suspend fun loadAllCustomTitles(): List<CustomTitle> {
        return dao.loadAllCustomTitles().map { it.toTitle() as CustomTitle }
    }

    fun loadActiveCryptoTitlesPagedAndObserved(): LiveData<PagedList<CryptoTitle>> {
        val factory = dao.loadAllActiveCryptoTitlesPagedAndObserved().map { it.toTitle() as CryptoTitle }
        val config = PagedList.Config.Builder().setPageSize(15).setInitialLoadSizeHint(30).setPrefetchDistance(15).setEnablePlaceholders(false).build()
        return LivePagedListBuilder<Int, CryptoTitle>(factory, config).build()
    }

    suspend fun insert(title: Title) =
            title.toEntity().let { dao.insert(it) }

    suspend fun insert(allTitles: Set<Title>) =
            allTitles.map { it.toEntity() }.let { dao.insert(it) }

    suspend fun update(title: Title) {
        dao.update(title.toEntity())
    }

    suspend fun update(allTitles: Set<Title>) {
        dao.update(allTitles.map { it.toEntity() })
    }

    suspend fun delete(titles: List<CustomTitle>) {
        dao.delete(titles.map { it.toEntity() })
    }

    suspend fun marketUpdate(titlesToInsert: Set<Title>, titlesToUpdate: Set<Title>, titlesToDelete: Set<Title>) {
        dao.marketUpdate(titlesToInsert.map { it.toEntity() }, titlesToUpdate.map { it.toEntity() }, titlesToDelete.map { it.toEntity() })
    }

    private val dao: TitleEntityDao
        get() {
            return database.titleDao()
        }

    private val database: Database
        get() {
            return Database.getInstance()
        }
}