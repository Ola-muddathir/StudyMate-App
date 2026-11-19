package com.example.studymate.ui.courses

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studymate.data.Course
import com.example.studymate.data.Task
import com.example.studymate.ui.theme.StudyMateTheme

@Composable
fun CoursesScreen(
    viewModel: CourseViewModel
) {
    val courses by viewModel.courses.collectAsState()
    val deleteError by viewModel.deleteError.collectAsState()
    val formError by viewModel.formError.collectAsState()

    var showForm by remember { mutableStateOf(false) }
    var selectedCourse by remember { mutableStateOf<Course?>(null) }
    var courseToDelete by remember { mutableStateOf<Course?>(null) }

    val primary = MaterialTheme.colorScheme.onSurface
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 22.dp, vertical = 20.dp)
    ) {
        Text(
            text = "Study.",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            color = primary
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Your courses.",
            fontSize = 29.sp,
            fontWeight = FontWeight.Light,
            color = primary
        )

        Text(
            text = "KEEP TRACK OF YOUR SUBJECTS",
            fontSize = 10.sp,
            letterSpacing = 3.sp,
            color = muted
        )

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My courses",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = primary
            )

            Text(
                text = "${courses.size} ITEMS",
                fontSize = 10.sp,
                letterSpacing = 2.sp,
                color = muted
            )
        }

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (courses.isEmpty()) {
                Text(
                    text = "No courses here yet.",
                    color = muted,
                    fontSize = 17.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(courses, key = { it.courseId }) { course ->

                        val tasks by remember(course.courseId) {
                            viewModel.tasksForCourse(course.courseId)
                        }.collectAsState(initial = emptyList())

                        CourseCard(
                            course = course,
                            tasks = tasks,
                            onEdit = {
                                viewModel.clearFormError()
                                selectedCourse = course
                                showForm = true
                            },
                            onDelete = {
                                courseToDelete = course
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Button(
            onClick = {
                viewModel.clearFormError()
                selectedCourse = null
                showForm = true
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
                text = "+   Add new course",
                fontSize = 16.sp
            )
        }
    }

    if (showForm) {
        CourseForm(
            initialName = selectedCourse?.courseName ?: "",
            initialCode = selectedCourse?.courseCode ?: "",
            title = if (selectedCourse == null) "Add Course" else "Edit Course",
            errorMessage = formError,
            onInputChange = {
                viewModel.clearFormError()
            },
            onSave = { name, code ->
                viewModel.saveCourse(selectedCourse, name, code) {
                    showForm = false
                }
            },
            onCancel = {
                viewModel.clearFormError()
                showForm = false
            }
        )
    }

    courseToDelete?.let { course ->
        AlertDialog(
            onDismissRequest = {
                courseToDelete = null
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    "Delete Course",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete ${course.courseName}?",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteCourse(course)
                        courseToDelete = null
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        courseToDelete = null
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

    deleteError?.let { error ->
        AlertDialog(
            onDismissRequest = {
                viewModel.clearDeleteError()
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    "Cannot Delete Course",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            text = {
                Text(
                    error,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearDeleteError()
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
private fun CourseCard(
    course: Course,
    tasks: List<Task>,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val completed = tasks.count { it.isCompleted }

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
            Text(
                text = course.courseName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(5.dp))

            Text(
                text = course.courseCode.uppercase(),
                fontSize = 11.sp,
                letterSpacing = 1.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = if (tasks.isEmpty()) {
                    "No tasks yet"
                } else {
                    "${tasks.size} tasks · $completed completed"
                },
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            tasks.take(3).forEach { task ->
                Text(
                    text = (if (task.isCompleted) "✓ " else "• ") + task.title,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            if (tasks.size > 3) {
                Text(
                    text = "+ ${tasks.size - 3} more",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(14.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(Modifier.height(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Edit")
                }

                Button(
                    onClick = onDelete,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Delete")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CourseCardPreview() {
    StudyMateTheme(dynamicColor = false) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CourseCard(
                course = Course(1, "Mobile Development", "CS301"),
                tasks = listOf(
                    Task(
                        taskId = 1,
                        title = "Homework 1",
                        courseId = 1,
                        dueDate = 0L
                    ),
                    Task(
                        taskId = 2,
                        title = "Final project",
                        courseId = 1,
                        dueDate = 0L,
                        isCompleted = true
                    )
                ),
                onEdit = {},
                onDelete = {}
            )

            CourseCard(
                course = Course(2, "Databases", "CS240"),
                tasks = emptyList(),
                onEdit = {},
                onDelete = {}
            )
        }
    }
}