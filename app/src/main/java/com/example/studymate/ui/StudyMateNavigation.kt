
package com.example.studymate.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.studymate.ui.dashboard.DashboardViewModel
import com.example.studymate.ui.dashboard.HomeScreen
import com.example.studymate.ui.courses.CourseViewModel
import com.example.studymate.ui.courses.CoursesScreen
import com.example.studymate.ui.tasks.TaskViewModel
import com.example.studymate.ui.tasks.TasksScreen

@Composable
fun StudyMateNavigation(
    dashboardViewModel: DashboardViewModel,
    courseViewModel: CourseViewModel,
    taskViewModel: TaskViewModel
) {
    var selectedScreen by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedScreen == 0,
                    onClick = { selectedScreen = 0 },
                    icon = { Text("🏠") },
                    label = { Text("Home") }
                )

                NavigationBarItem(
                    selected = selectedScreen == 1,
                    onClick = { selectedScreen = 1 },
                    icon = { Text("📚") },
                    label = { Text("Courses") }
                )

                NavigationBarItem(
                    selected = selectedScreen == 2,
                    onClick = { selectedScreen = 2 },
                    icon = { Text("📝") },
                    label = { Text("Tasks") }
                )

                NavigationBarItem(
                    selected = selectedScreen == 3,
                    onClick = { selectedScreen = 3 },
                    icon = { Text("⚙️") },
                    label = { Text("Settings") }
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedScreen) {
                0 -> HomeScreen(dashboardViewModel)
                1 -> CoursesScreen(courseViewModel)
                2 -> TasksScreen(taskViewModel)
                3 -> SettingsScreen()
            }
        }
    }
}
