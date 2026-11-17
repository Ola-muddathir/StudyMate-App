package com.example.studymate.ui.courses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.studymate.data.Course
import com.example.studymate.data.CourseDao
import com.example.studymate.data.Task
import com.example.studymate.data.TaskDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CourseViewModel(
    private val courseDao: CourseDao,
    private val taskDao: TaskDao
) : ViewModel() {

    val courses = courseDao.getAllCourses()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError = _deleteError.asStateFlow()

    private val _formError = MutableStateFlow<String?>(null)
    val formError = _formError.asStateFlow()

    fun tasksForCourse(courseId: Int): Flow<List<Task>> =
        taskDao.getTasksByCourse(courseId)

    /**
     * Adds a new course (existing == null) or updates an existing one.
     * Calls onSuccess only if validation passed.
     */
    fun saveCourse(
        existing: Course?,
        name: String,
        code: String,
        onSuccess: () -> Unit
    ) {
        val cleanName = name.trim()
        val cleanCode = code.trim()

        if (cleanName.isBlank() || cleanCode.isBlank()) {
            _formError.value = "Please fill in both fields."
            return
        }

        val duplicate = courses.value.any {
            it.courseCode.equals(cleanCode, ignoreCase = true) &&
                    it.courseId != existing?.courseId
        }
        if (duplicate) {
            _formError.value = "A course with this code already exists."
            return
        }

        _formError.value = null
        viewModelScope.launch {
            if (existing == null) {
                courseDao.insertCourse(
                    Course(courseName = cleanName, courseCode = cleanCode)
                )
            } else {
                courseDao.updateCourse(
                    existing.copy(courseName = cleanName, courseCode = cleanCode)
                )
            }
            onSuccess()
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            if (taskDao.countTasksByCourse(course.courseId) > 0) {
                _deleteError.value =
                    "Cannot delete this course because it has tasks assigned to it."
            } else {
                courseDao.deleteCourse(course)
            }
        }
    }

    fun clearDeleteError() {
        _deleteError.value = null
    }

    fun clearFormError() {
        _formError.value = null
    }
}

class CourseViewModelFactory(
    private val courseDao: CourseDao,
    private val taskDao: TaskDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CourseViewModel(courseDao, taskDao) as T
    }
}