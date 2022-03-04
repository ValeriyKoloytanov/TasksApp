package com.koloytanov.tasksApp.fragment

import androidx.lifecycle.*
import com.koloytanov.tasksApp.model.Task
import com.koloytanov.tasksApp.reminderRoom.Taskrepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class ToDoViewModel(private val taskRepo: Taskrepository) : ViewModel() {
    var id = 0
    val Notes: LiveData<List<Task>> = taskRepo.getNotes().asLiveData()
    var task: Task? = null
    fun insert(task: Task) = viewModelScope.launch {
        taskRepo.insert(task)
    }

    fun update(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        taskRepo.update(task)
    }

    fun delete(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        taskRepo.delete(task)
    }

    fun specific_task(id: Int): Task = runBlocking {
        taskRepo.getclickedtask(id)
    }
}

class TaskModelFactory(private val repository: Taskrepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(ToDoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ToDoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

