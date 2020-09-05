package com.ceaver.assin.action.list

import androidx.lifecycle.ViewModel
import com.ceaver.assin.action.ActionRepository

class ActionListViewModel :  ViewModel() {

    val titles = ActionRepository.loadAllActionsObserved()
}