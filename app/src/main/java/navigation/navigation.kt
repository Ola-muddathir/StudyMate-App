
package com.example.studymate.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
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
    val navController = rememberNavController()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = currentRoute == "home",
                    onClick = {
                        navController.navigate("home") {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Text("🏠")
                    },
                    label = {
                        Text("Home")
                    }
                )

                NavigationBarItem(
                    selected = currentRoute == "courses",
                    onClick = {
                        navController.navigate("courses") {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Text("📚")
                    },
                    label = {
                        Text("Courses")
                    }
                )

                NavigationBarItem(
                    selected = currentRoute == "tasks",
                    onClick = {
                        navController.navigate("tasks") {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Text("📝")
                    },
                    label = {
                        Text("Tasks")
                    }
                )
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            composable("home") {
                HomeScreen(
                    viewModel = dashboardViewModel
                )
            }

            composable("courses") {
                CoursesScreen(
                    viewModel = courseViewModel
                )
            }

            composable("tasks") {
                TasksScreen(
                    viewModel = taskViewModel
                )
            }
        }
    }
}
