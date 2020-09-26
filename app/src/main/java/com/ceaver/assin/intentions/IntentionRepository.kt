package com.ceaver.assin.intentions

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.ceaver.assin.database.Database

object IntentionRepository {

    suspend fun loadById(id: Long): Intention =
            dao.loadById(id).toIntention()

    fun loadAll(): List<Intention> =
            dao.loadAll().map { it.toIntention() }

    fun loadAllObserved(): LiveData<List<Intention>> =
            Transformations.map(dao.loadAllObserved()) { it.map { it.toIntention() } }

    suspend fun insert(intention: Intention) =
            intention.toIntentionEntity().let { dao.insert(it) }

    suspend fun insert(intentions: List<Intention>) =
            intentions.map { it.toIntentionEntity() }.let { dao.insert(it) }

    suspend fun saveIntention(intention: Intention) {
        if (intention.id > 0)
            update(intention)
        else
            insert(intention)
    }

    suspend fun update(intention: Intention) {
        dao.update(intention.toIntentionEntity())
    }

    suspend fun delete(intention: Intention) {
        dao.delete(intention.toIntentionEntity())
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }

    private val dao: IntentionEntityDao
        get() = database.intentionDao()

    private val database: Database
        get() = Database.getInstance()
}