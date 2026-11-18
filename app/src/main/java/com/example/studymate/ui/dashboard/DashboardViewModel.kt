package com.example.studymate.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.studymate.data.TaskDao
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class DashboardUiState(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val upcomingTasks: Int = 0,
    val overdueTasks: Int = 0
)

class DashboardViewModel(
    taskDao: TaskDao
) : ViewModel() {

    val dashboardState: StateFlow<DashboardUiState> =
        taskDao.getAllTasks()
            .map { tasks ->

                val currentTime = System.currentTimeMillis()

                DashboardUiState(
                    totalTasks = tasks.size,

                    completedTasks = tasks.count {
                        it.isCompleted
                    },

                    upcomingTasks = tasks.count {
                        !it.isCompleted &&
                                it.dueDate > currentTime
                    },

                    overdueTasks = tasks.count {
                        !it.isCompleted &&
                                it.dueDate < currentTime
                    }
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = DashboardUiState()
            )
}
class DashboardViewModelFactory(
    private val taskDao: TaskDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return DashboardViewModel(taskDao) as T
    }
}
