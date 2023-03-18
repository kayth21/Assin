package com.ceaver.assin.intentions

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.ceaver.assin.database.Database

object IntentionRepository {

    suspend fun loadById(id: Long): Intention =
            dao.loadById(id).toIntention()

    suspend fun loadAll(): List<Intention> =
            dao.loadAll().map { it.toIntention() }

    fun loadAllObserved(): LiveData<List<Intention>> = dao.loadAllObserved().map { it.map { it.toIntention() } }

    suspend fun insert(intention: Intention) =
            intention.toEntity().let { dao.insert(it) }

    suspend fun insert(intentions: List<Intention>) =
            intentions.map { it.toEntity() }.let { dao.insert(it) }

    suspend fun update(intention: Intention) =
            intention.toEntity().let { dao.update(it) }

    suspend fun delete(intention: Intention) =
            intention.toEntity().let { dao.delete(it) }

    suspend fun deleteAll() =
            dao.deleteAll()

    suspend fun save(intention: Intention) =
            if (intention.id > 0) update(intention) else insert(intention)

    private val dao: IntentionEntityDao
        get() = database.intentionDao()

    private val database: Database
        get() = Database.getInstance()
}