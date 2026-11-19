
package com.example.studymate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider

import com.example.studymate.data.AppDatabase
import com.example.studymate.data.PreferencesRepository

import com.example.studymate.ui.theme.StudyMateTheme

import com.example.studymate.ui.dashboard.DashboardViewModel
import com.example.studymate.ui.dashboard.DashboardViewModelFactory

import com.example.studymate.ui.courses.CourseViewModel
import com.example.studymate.ui.courses.CourseViewModelFactory

import com.example.studymate.ui.tasks.TaskViewModel
import com.example.studymate.ui.tasks.TaskViewModelFactory

import com.example.studymate.ui.StudyMateNavigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)

        val dashboardViewModel = ViewModelProvider(
            this,
            DashboardViewModelFactory(database.taskDao())
        )[DashboardViewModel::class.java]

        val courseViewModel = ViewModelProvider(
            this,
            CourseViewModelFactory(
                database.courseDao(),
                database.taskDao()
            )
        )[CourseViewModel::class.java]

        val taskViewModel = ViewModelProvider(
            this,
            TaskViewModelFactory(
                database.taskDao(),
                database.courseDao()
            )
        )[TaskViewModel::class.java]

        setContent {
            val context = LocalContext.current

            val preferencesRepository = remember {
                PreferencesRepository(context)
            }

            val themeMode by preferencesRepository.themeMode.collectAsState(
                initial = "SYSTEM"
            )

            val darkTheme = when (themeMode) {
                "DARK" -> true
                "LIGHT" -> false
                else -> isSystemInDarkTheme()
            }

            StudyMateTheme(
                darkTheme = darkTheme,
                dynamicColor = false
            ) {
                StudyMateNavigation(
                    dashboardViewModel = dashboardViewModel,
                    courseViewModel = courseViewModel,
                    taskViewModel = taskViewModel
                )
            }
        }
    }
}
