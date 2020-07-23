package com.ceaver.assin.action

import android.os.Handler
import android.os.Looper
import com.ceaver.assin.database.Database
import com.ceaver.assin.positions.PositionRepository.insertPosition
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


    fun loadActions(symbol: String): List<Action> {
        return getActionDao().loadAllActions().filter { it.buyTitle?.symbol == symbol || it.sellTitle?.symbol == symbol }
    }

    fun insertDeposit(action: Action) {
        insertAction(action)
        insertPosition(action)
    }

    fun insertDepositAsync(action: Action, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute {
            insertDeposit(action)
            if (callbackInMainThread) Handler(Looper.getMainLooper()).post(callback) else callback.invoke()
        }
    }

    fun insertTrade(action: Action) {
        insertAction(action)
        // TODO
    }

    fun insertTradeAsync(action: Action, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute {
            insertTrade(action)
            if (callbackInMainThread) Handler(Looper.getMainLooper()).post(callback) else callback.invoke()
        }
    }

    fun insertWithdraw(action: Action) {
        insertAction(action)
        // TODO
    }

    fun insertWithdrawAsync(action: Action, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute {
            insertWithdraw(action)
            if (callbackInMainThread) Handler(Looper.getMainLooper()).post(callback) else callback.invoke()
        }
    }

    fun insertSplit(action: Action) {
        TODO()
    }

    fun insertMerge(action: Action) {
        TODO()
    }

    fun insertActions(alerts: List<Action>) {
        alerts.forEach {
            when (it.getActionType()) {
                ActionType.DEPOSIT -> insertDeposit(it)
                ActionType.TRADE -> insertTrade(it)
                ActionType.WITHDRAW -> insertWithdraw(it)
            }
        }
        EventBus.getDefault().post(ActionEvents.Insert())
    }

    fun insertActionsAsync(actions: List<Action>, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute { insertActions(actions); if (callbackInMainThread) Handler(Looper.getMainLooper()).post(callback) else callback.invoke() }
    }

    private fun insertActionAsync(action: Action, callbackInMainThread: Boolean, callback: () -> Unit) {
        BackgroundThreadExecutor.execute {
            insertAction(action)
            if (callbackInMainThread) Handler(Looper.getMainLooper()).post(callback) else callback.invoke()
        }
    }

    private fun insertAction(action: Action) {
        getActionDao().insertAction(action)
        EventBus.getDefault().post(ActionEvents.Insert())
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