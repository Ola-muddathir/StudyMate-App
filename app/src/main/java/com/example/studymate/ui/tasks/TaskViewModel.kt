
package com.example.studymate.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.studymate.data.Course
import com.example.studymate.data.CourseDao
import com.example.studymate.data.Priority
import com.example.studymate.data.Task
import com.example.studymate.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TaskUiModel(
    val taskId: Int,
    val title: String,
    val description: String,
    val courseId: Int,
    val courseName: String,
    val dueDate: Long,
    val priority: String,
    val isCompleted: Boolean,
    val createdAt: Long
) {
    fun toEntity(): Task = Task(
        taskId = taskId,
        title = title,
        description = description,
        courseId = courseId,
        dueDate = dueDate,
        priority = priority,
        isCompleted = isCompleted,
        createdAt = createdAt
    )
}

class TaskViewModel(
    private val taskDao: TaskDao,
    private val courseDao: CourseDao
) : ViewModel() {

    val courses: StateFlow<List<Course>> = courseDao.getAllCourses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val tasks: StateFlow<List<TaskUiModel>> = combine(
        taskDao.getAllTasks(),
        courseDao.getAllCourses()
    ) { taskList, courseList ->

        val courseMap = courseList.associateBy { it.courseId }

        taskList.map { task ->
            val course = courseMap[task.courseId]

            val displayCourse = if (course != null) {
                "${course.courseCode.uppercase()} · ${course.courseName}"
            } else {
                "Unknown Course"
            }

            TaskUiModel(
                taskId = task.taskId,
                title = task.title,
                description = task.description,
                courseId = task.courseId,
                courseName = displayCourse,
                dueDate = task.dueDate,
                priority = task.priority,
                isCompleted = task.isCompleted,
                createdAt = task.createdAt
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _formError = MutableStateFlow<String?>(null)
    val formError: StateFlow<String?> = _formError.asStateFlow()

    fun saveTask(
        existingTask: TaskUiModel?,
        title: String,
        description: String,
        courseId: Int?,
        dueDate: Long,
        priority: String,
        onSuccess: () -> Unit
    ) {
        val cleanTitle = title.trim()
        val cleanDescription = description.trim()

        when {
            cleanTitle.isBlank() -> {
                _formError.value = "Please enter a task title."
                return
            }

            courseId == null || courses.value.none {
                it.courseId == courseId
            } -> {
                _formError.value = "Please select a valid course."
                return
            }

            !Priority.isValid(priority) -> {
                _formError.value = "Please select a valid priority."
                return
            }
        }

        _formError.value = null

        viewModelScope.launch {
            try {
                if (existingTask == null) {
                    taskDao.insertTask(
                        Task(
                            title = cleanTitle,
                            description = cleanDescription,
                            courseId = courseId,
                            dueDate = dueDate,
                            priority = priority
                        )
                    )
                } else {
                    taskDao.updateTask(
                        existingTask.toEntity().copy(
                            title = cleanTitle,
                            description = cleanDescription,
                            courseId = courseId,
                            dueDate = dueDate,
                            priority = priority
                        )
                    )
                }

                onSuccess()
            } catch (e: Exception) {
                android.util.Log.e("TaskViewModel", "Save failed", e)
            }
        }
    }

    fun toggleTaskCompletion(task: TaskUiModel) {
        viewModelScope.launch {
            taskDao.updateTask(
                task.toEntity().copy(
                    isCompleted = !task.isCompleted
                )
            )
        }
    }

    // Delete task using its ID
    fun deleteTask(task: TaskUiModel) {
        viewModelScope.launch {
            try {
                val deletedRows = taskDao.deleteTaskById(task.taskId)

                android.util.Log.d(
                    "TaskDelete",
                    "Deleted rows: $deletedRows, Task ID: ${task.taskId}"
                )
            } catch (e: Exception) {
                android.util.Log.e(
                    "TaskDelete",
                    "Delete failed",
                    e
                )
            }
        }
    }

    fun clearFormError() {
        _formError.value = null
    }
}

class TaskViewModelFactory(
    private val taskDao: TaskDao,
    private val courseDao: CourseDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TaskViewModel(taskDao, courseDao) as T
    }
}
