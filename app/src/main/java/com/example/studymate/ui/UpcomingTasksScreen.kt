
package com.example.studymate.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.studymate.data.Task
import com.example.studymate.data.TaskDao
import com.example.studymate.data.CourseDao
import com.example.studymate.utils.ShareUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class UpcomingTasksScreen {


@Composable
fun UpcomingTasksScreen(
    taskDao: TaskDao,
    courseDao: CourseDao
) {

    val context = LocalContext.current

    val tasks by taskDao.getAllTasks()
        .collectAsState(initial = emptyList())

    val courses by courseDao.getAllCourses()
        .collectAsState(initial = emptyList())

    val currentTime = System.currentTimeMillis()

    val upcomingTasks = tasks
        .filter {
            !it.isCompleted && it.dueDate >= currentTime
        }
        .sortedBy { it.dueDate }

    val overdueTasks = tasks
        .filter {
            !it.isCompleted && it.dueDate < currentTime
        }
        .sortedBy { it.dueDate }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Deadlines",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Text(
                    text = "Upcoming Tasks (${upcomingTasks.size})",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (upcomingTasks.isEmpty()) {
                item {
                    Text("No upcoming tasks")
                }
            }

            items(
                items = upcomingTasks,
                key = { it.taskId }
            ) { task ->

                TaskDeadlineCard(
                    task = task,
                    courseName = courses.firstOrNull {
                        it.courseId == task.courseId
                    }?.courseName ?: "Unknown Course",
                    onShare = {
                        ShareUtils().shareTask(
                            context = context,
                            title = task.title,
                            description = task.description,
                            dueDate = formatDate(task.dueDate),
                            priority = task.priority
                        )
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Overdue Tasks (${overdueTasks.size})",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            if (overdueTasks.isEmpty()) {
                item {
                    Text("No overdue tasks")
                }
            }

            items(
                items = overdueTasks,
                key = { it.taskId }
            ) { task ->

                TaskDeadlineCard(
                    task = task,
                    courseName = courses.firstOrNull {
                        it.courseId == task.courseId
                    }?.courseName ?: "Unknown Course",
                    onShare = {
                        ShareUtils().shareTask(
                            context = context,
                            title = task.title,
                            description = task.description,
                            dueDate = formatDate(task.dueDate),
                            priority = task.priority
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun TaskDeadlineCard(
    task: Task,
    courseName: String,
    onShare: () -> Unit
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = task.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(text = "Course: $courseName")

            Text(text = "Due Date: ${formatDate(task.dueDate)}")

            Text(text = "Priority: ${task.priority}")

            if (task.description.isNotBlank()) {
                Text(text = task.description)
            }

            Button(
                onClick = onShare
            ) {
                Text("Share Task")
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {

    val formatter = SimpleDateFormat(
        "dd/MM/yyyy",
        Locale.getDefault()
    )

    return formatter.format(Date(timestamp))
}


}