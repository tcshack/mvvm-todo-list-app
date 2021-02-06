package com.tcscorp.mvvmarchitecture.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tcscorp.mvvmarchitecture.data.Task
import com.tcscorp.mvvmarchitecture.data.TaskDao
import com.tcscorp.mvvmarchitecture.ui.ADD_TASK_RESULT_OK
import com.tcscorp.mvvmarchitecture.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Created on 31/01/2021 21:18
 * @author tcscorp
 */
class AddEditTaskViewModel @ViewModelInject constructor(
    @Assisted private val state: SavedStateHandle,
    private val taskDao: TaskDao
) : ViewModel() {

    private val taskAddEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent = taskAddEditTaskEventChannel.receiveAsFlow()

    val task = state.get<Task>("task")
    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }
    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskImportance", value)
        }

    fun onSaveClick() {
        if (taskName.isBlank()) {
            showInvalidInputMessage()
            return
        }
        if (task != null) {
            val updatedTask = task.copy(name = taskName, important = taskImportance)
            updateTask(updatedTask)
        } else {
            val newTask = Task(taskName, taskImportance)
            createTask(newTask)
        }
    }

    private fun showInvalidInputMessage() =
        viewModelScope.launch {
            taskAddEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage("Name cannot be empty"))
        }

    private fun updateTask(task: Task) =
        viewModelScope.launch {
            taskDao.update(task)
            taskAddEditTaskEventChannel.send(
                AddEditTaskEvent.NavigateBackWithResult(
                    EDIT_TASK_RESULT_OK
                )
            )
        }

    private fun createTask(task: Task) =
        viewModelScope.launch {
            taskDao.insert(task)
            taskAddEditTaskEventChannel.send(
                AddEditTaskEvent.NavigateBackWithResult(
                    ADD_TASK_RESULT_OK
                )
            )
        }

    sealed class AddEditTaskEvent {
        data class ShowInvalidInputMessage(val message: String) : AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int) : AddEditTaskEvent()
    }

}