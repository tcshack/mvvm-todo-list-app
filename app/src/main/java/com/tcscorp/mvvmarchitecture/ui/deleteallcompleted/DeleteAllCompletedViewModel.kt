package com.tcscorp.mvvmarchitecture.ui.deleteallcompleted

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.tcscorp.mvvmarchitecture.data.TaskDao
import com.tcscorp.mvvmarchitecture.di.ApplicationScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * Created on 01/02/2021 01:21
 * @author tcscorp
 */
class DeleteAllCompletedViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @ApplicationScope private val applicationScope: CoroutineScope
) : ViewModel() {

    fun onConfirmClick() = applicationScope.launch {
        taskDao.deleteCompletedTasks()
    }

}