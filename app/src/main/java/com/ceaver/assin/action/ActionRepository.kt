package com.ceaver.assin.action

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.database.Database
import com.ceaver.assin.threading.BackgroundThreadExecutor
import org.greenrobot.eventbus.EventBus

object ActionRepository {

    fun loadAction(id: Long): Action {
        return getActionDao().loadAction(id)
    }

    fun loadActionAsync(id: Long, callbackInMainThread: Boolean, callback: (Action) -> Unit) {
        BackgroundThreadExecutor.execute {
            val action = getActionDao().loadAction(id)
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(action) }
            else {
                callback.invoke(action);
            }
        }
    }

    fun loadAllActions(): List<Action> {
        return getActionDao().loadAllActions()
    }

    fun loadAllActionsAsync(callbackInMainThread: Boolean, callback: (List<Action>) -> Unit) {
        BackgroundThreadExecutor.execute {
            val actions = loadAllActions()
            if (callbackInMainThread)
                Handler(Looper.getMainLooper()).post { callback.invoke(actions) }
            else
                callback.invoke(actions)
        }
    }

    fun saveAction(action: Action) {
        if (action.id > 0) updateAction(action) else insertAction(action)
    }

    fun saveActionAsync(action: Action, callbackInMainThread: Boolean, callback: () -> Unit) {
        if (action.id > 0) updateActionAsync(action, callbackInMainThread, callback) else insertActionAsync(action, callbackInMainThread, callback)
    }

    fun insertAction(action: Action) {
        getActionDao().insertAction(action); EventBus.getDefault().post(ActionEvents.Insert())
    }

    fun insertActionAsync(action: Action, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute { insertAction(action); if (callbackInMainThread) Handler(Looper.getMainLooper()).post(callback) else callback.invoke() }
    }

    fun insertActions(alerts: List<Action>) {
        getActionDao().insertActions(alerts); EventBus.getDefault().post(ActionEvents.Insert())
    }

    fun insertActionsAsync(actions: List<Action>, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute { insertActions(actions); if (callbackInMainThread) Handler(Looper.getMainLooper()).post(callback) else callback.invoke() }
    }

    fun updateAction(action: Action) {
        getActionDao().updateAction(action); EventBus.getDefault().post(ActionEvents.Update())
    }

    fun updateActionAsync(action: Action, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute { updateAction(action); if (callbackInMainThread) Handler(Looper.getMainLooper()).post(callback) else callback.invoke() }
    }

    fun deleteAction(action: Action) {
        getActionDao().deleteAction(action); EventBus.getDefault().post(ActionEvents.Delete())
    }

    fun deleteActionAsync(action: Action) {
        BackgroundThreadExecutor.execute { deleteAction(action) }
    }

    fun deleteAllActions() {
        getActionDao().deleteAllActions(); EventBus.getDefault().post(ActionEvents.DeleteAll())
    }

    fun deleteAllActionsAsync() {
        deleteAllActionsAsync() {}
    }

    fun deleteAllActionsAsync(callback: () -> Unit) {
        BackgroundThreadExecutor.execute { deleteAllActions(); callback.invoke() }
    }

    private fun getActionDao(): ActionDao {
        return getDatabase().actionDao()
    }

    private fun getDatabase(): Database {
        return Database.getInstance()
    }
}