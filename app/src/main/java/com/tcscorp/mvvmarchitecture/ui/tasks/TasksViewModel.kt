package com.tcscorp.mvvmarchitecture.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.tcscorp.mvvmarchitecture.data.PreferencesManager
import com.tcscorp.mvvmarchitecture.data.SortOrder
import com.tcscorp.mvvmarchitecture.data.Task
import com.tcscorp.mvvmarchitecture.data.TaskDao
import com.tcscorp.mvvmarchitecture.ui.ADD_TASK_RESULT_OK
import com.tcscorp.mvvmarchitecture.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

/**
 * Created on 31/01/2021 15:22
 * @author tcscorp
 */
class TasksViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager,
    @Assisted private val state: SavedStateHandle
) : ViewModel() {

    private val tasksEventChannel = Channel<TasksEvent>()
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    val searchQuery = state.getLiveData("searchQuery", "")
    val preferencesFlow = preferencesManager.preferencesFlow
    private val tasksFlow = combine(
        searchQuery.asFlow(),
        preferencesFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }
    val tasks = tasksFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClicked(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelect(task: Task) {
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.NavigateToEditTaskScreen(task))
        }
    }

    fun onTaskCheckedChanged(task: Task, checked: Boolean) {
        viewModelScope.launch {
            taskDao.update(task.copy(completed = checked))
        }
    }

    fun onTaskSwiped(task: Task) =
        viewModelScope.launch {
            taskDao.delete(task)
            tasksEventChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
        }

    fun onUndoDeleteClick(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task)
        }
    }

    fun onAddNewTaskClick() {
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.NavigateToAddTaskScreen)
        }
    }

    fun onAddEditEvent(result: Int) {
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
        }
    }

    private fun showTaskSavedConfirmationMessage(message: String) = viewModelScope.launch {
        tasksEventChannel.send(TasksEvent.ShowTaskSavedConfirmationMessage(message))
    }

    fun onDeleteAllCompletedClick() =
        viewModelScope.launch {
            tasksEventChannel.send(TasksEvent.NavigateToDeleteAllCompletedScreen)
        }

    sealed class TasksEvent {
        object NavigateToAddTaskScreen : TasksEvent()
        object NavigateToDeleteAllCompletedScreen : TasksEvent()
        data class NavigateToEditTaskScreen(val task: Task) : TasksEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TasksEvent()
        data class ShowTaskSavedConfirmationMessage(val message: String) : TasksEvent()
    }

}