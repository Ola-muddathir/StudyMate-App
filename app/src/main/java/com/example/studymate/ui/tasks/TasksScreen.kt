
package com.example.studymate.ui.tasks

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TasksScreen(
    viewModel: TaskViewModel,
    onShareTask: ((TaskUiModel) -> Unit)? = null
) {
    val context = LocalContext.current
    val tasks by viewModel.tasks.collectAsState()
    val courses by viewModel.courses.collectAsState()
    val formError by viewModel.formError.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    var showForm by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<TaskUiModel?>(null) }
    var taskToDelete by remember { mutableStateOf<TaskUiModel?>(null) }
    var showNoCourseAlert by remember { mutableStateOf(false) }

    if (showForm) {
        TaskForm(
            courses = courses,
            initialTitle = selectedTask?.title ?: "",
            initialDescription = selectedTask?.description ?: "",
            initialCourseId = selectedTask?.courseId
                ?: courses.firstOrNull()?.courseId,
            initialDueDate = selectedTask?.dueDate
                ?: System.currentTimeMillis(),
            initialPriority = selectedTask?.priority ?: "MEDIUM",
            errorMessage = formError,
            onInputChange = {
                viewModel.clearFormError()
            },
            onSave = { title, description, courseId, dueDate, priority ->
                viewModel.saveTask(
                    existingTask = selectedTask,
                    title = title,
                    description = description,
                    courseId = courseId,
                    dueDate = dueDate,
                    priority = priority
                ) {
                    showForm = false
                    selectedTask = null
                }
            },
            onCancel = {
                viewModel.clearFormError()
                showForm = false
                selectedTask = null
            }
        )
        return
    }

    val filteredTasks = when (selectedFilter) {
        "Active" -> tasks.filter { !it.isCompleted }
        "Completed" -> tasks.filter { it.isCompleted }
        else -> tasks
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 20.dp)
    ) {

        Text(
            text = "Study.",
            fontSize = 34.sp,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your assignments.",
            fontSize = 29.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Text(
            text = "KEEP TRACK OF YOUR ACADEMIC WORK",
            fontSize = 10.sp,
            letterSpacing = 3.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My tasks",
                fontSize = 24.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "${filteredTasks.size} ITEMS",
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "Active", "Completed").forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = {
                        selectedFilter = filter
                    },
                    label = {
                        Text(filter)
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surface,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (filteredTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No tasks here yet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 17.sp
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                filteredTasks.forEach { task ->
                    TaskCard(
                        task = task,
                        onDetails = {
                            val intent = Intent(
                                context,
                                TaskDetailActivity::class.java
                            ).apply {
                                putExtra("EXTRA_TITLE", task.title)
                                putExtra("EXTRA_COURSE", task.courseName)
                                putExtra("EXTRA_DESC", task.description)
                                putExtra("EXTRA_PRIORITY", task.priority)
                                putExtra("EXTRA_DUE_DATE", task.dueDate)
                                putExtra("EXTRA_COMPLETED", task.isCompleted)
                            }

                            context.startActivity(intent)
                        },
                        onEdit = {
                            viewModel.clearFormError()
                            selectedTask = task
                            showForm = true
                        },
                        onDelete = {
                            taskToDelete = task
                        },
                        onToggleComplete = {
                            viewModel.toggleTaskCompletion(task)
                        },
                        onShare = onShareTask?.let { share ->
                            { share(task) }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (courses.isEmpty()) {
                    showNoCourseAlert = true
                } else {
                    viewModel.clearFormError()
                    selectedTask = null
                    showForm = true
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = "+   Add new task",
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }

    taskToDelete?.let { task ->
        AlertDialog(
            onDismissRequest = {
                taskToDelete = null
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    "Delete Task",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete \"${task.title}\"?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteTask(task)
                    taskToDelete = null
                }) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        taskToDelete = null
                    }
                ) {
                    Text(
                        "Cancel",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        )
    }

    if (showNoCourseAlert) {
        AlertDialog(
            onDismissRequest = {
                showNoCourseAlert = false
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    "No Courses Found",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    "Please add at least one course in the Courses screen before creating a task.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showNoCourseAlert = false
                    }
                ) {
                    Text(
                        "OK",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
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
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = title,
                fontSize = 9.sp,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                fontSize = 27.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun TaskCard(
    task: TaskUiModel,
    onDetails: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit,
    onShare: (() -> Unit)? = null
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
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(17.dp)
        ) {
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(13.dp))

            Text(
                text = task.title,
                fontSize = 20.sp,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = task.courseName,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (task.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = task.description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "DUE DATE",
                fontSize = 9.sp,
                letterSpacing = 2.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = SimpleDateFormat(
                    "dd MMM yyyy",
                    Locale.getDefault()
                ).format(Date(task.dueDate)),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onToggleComplete,
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Text(
                        text = if (task.isCompleted) "Incomplete" else "Complete",
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row {
                    if (onShare != null) {
                        TextButton(
                            onClick = onShare,
                            contentPadding = PaddingValues(horizontal = 6.dp)
                        ) {
                            Text(
                                "Share",
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    TextButton(
                        onClick = onDetails,
                        contentPadding = PaddingValues(horizontal = 6.dp)
                    ) {
                        Text(
                            "Details",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    TextButton(
                        onClick = onEdit,
                        contentPadding = PaddingValues(horizontal = 6.dp)
                    ) {
                        Text(
                            "Edit",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(
                        onClick = onDelete,
                        contentPadding = PaddingValues(horizontal = 6.dp)
                    ) {
                        Text(
                            "Delete",
                            color = Color(0xFFB65C70)
                        )
                    }
                }
            }
        }
    }
}
