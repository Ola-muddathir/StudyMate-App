package com.example.studymate.ui.tasks

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Temporary UI Model
data class TaskUiModel(
    val taskId: Int,
    val title: String,
    val description: String,
    val courseName: String,
    val dueDate: Long,
    val priority: String,
    val isCompleted: Boolean
)

// Pink Editorial Color Palette
private val BackgroundPink = Color(0xFFFAF2F3)
private val DarkBurgundy = Color(0xFF40232D)
private val RosePink = Color(0xFFE99BAD)
private val MutedRose = Color(0xFF967A83)
private val CardWhite = Color(0xFFFFFEFE)

@Composable
fun TasksScreen(
    tasks: List<TaskUiModel>,
    onAddTask: () -> Unit,
    onEditTask: (TaskUiModel) -> Unit,
    onDeleteTask: (TaskUiModel) -> Unit,
    onToggleComplete: (TaskUiModel) -> Unit
) {
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredTasks = when (selectedFilter) {
        "Active" -> tasks.filter { !it.isCompleted }
        "Completed" -> tasks.filter { it.isCompleted }
        else -> tasks
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundPink)
            .padding(horizontal = 22.dp, vertical = 20.dp)
    ) {

        // Header
        Text(
            text = "Study.",
            fontSize = 34.sp,
            color = DarkBurgundy,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your assignments.",
            fontSize = 29.sp,
            color = DarkBurgundy
        )

        Text(
            text = "KEEP TRACK OF YOUR ACADEMIC WORK",
            fontSize = 10.sp,
            letterSpacing = 3.sp,
            color = MutedRose
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Task Statistics
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatisticCard(
                title = "ALL TASKS",
                value = tasks.size.toString(),
                modifier = Modifier.weight(1f)
            )

            StatisticCard(
                title = "COMPLETED",
                value = tasks.count { it.isCompleted }.toString(),
                modifier = Modifier.weight(1f)
            )

            StatisticCard(
                title = "PENDING",
                value = tasks.count { !it.isCompleted }.toString(),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Section Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My tasks",
                fontSize = 24.sp,
                color = DarkBurgundy,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "${filteredTasks.size} ITEMS",
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = MutedRose
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filters
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Active", "Completed").forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = { Text(filter) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = DarkBurgundy,
                        selectedLabelColor = Color.White,
                        containerColor = CardWhite,
                        labelColor = MutedRose
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tasks List
        if (filteredTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tasks here yet.",
                    color = MutedRose,
                    fontSize = 17.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = filteredTasks,
                    key = { it.taskId }
                ) { task ->
                    TaskCard(
                        task = task,
                        onEdit = { onEditTask(task) },
                        onDelete = { onDeleteTask(task) },
                        onToggleComplete = { onToggleComplete(task) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Add Task Button
        Button(
            onClick = onAddTask,
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkBurgundy
            )
        ) {
            Text(
                text = "+   Add new task",
                fontSize = 16.sp,
                color = Color.White
            )
        }
    }
}

@Composable
private fun StatisticCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardWhite
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 9.sp,
                letterSpacing = 1.sp,
                color = MutedRose
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 27.sp,
                color = DarkBurgundy,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TaskCard(
    task: TaskUiModel,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit
) {
    val priorityColor = when (task.priority) {
        "HIGH" -> Color(0xFFB65C70)
        "MEDIUM" -> Color(0xFFC28B54)
        else -> Color(0xFF71866B)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(17.dp)
        ) {

            // Priority and Completion
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = priorityColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = task.priority,
                        color = priorityColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(
                            horizontal = 11.dp,
                            vertical = 6.dp
                        )
                    )
                }

                Text(
                    text = if (task.isCompleted) "COMPLETED" else "PENDING",
                    fontSize = 10.sp,
                    letterSpacing = 1.sp,
                    color = MutedRose
                )
            }

            Spacer(modifier = Modifier.height(13.dp))

            // Task Title
            Text(
                text = task.title,
                fontSize = 20.sp,
                color = DarkBurgundy,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = task.courseName,
                fontSize = 13.sp,
                color = MutedRose
            )

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = task.description,
                    fontSize = 13.sp,
                    color = MutedRose
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(color = BackgroundPink)

            Spacer(modifier = Modifier.height(10.dp))

            // Due Date
            Text(
                text = "DUE DATE",
                fontSize = 9.sp,
                letterSpacing = 2.sp,
                color = MutedRose
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale.getDefault()
                ).format(Date(task.dueDate)),
                fontSize = 14.sp,
                color = DarkBurgundy
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onToggleComplete
                ) {
                    Text(
                        text = if (task.isCompleted) {
                            "Mark incomplete"
                        } else {
                            "Complete"
                        },
                        color = DarkBurgundy
                    )
                }

                Row {
                    TextButton(onClick = onEdit) {
                        Text("Edit", color = MutedRose)
                    }

                    TextButton(onClick = onDelete) {
                        Text("Delete", color = Color(0xFFB65C70))
                    }
                }
            }
        }
    }
}